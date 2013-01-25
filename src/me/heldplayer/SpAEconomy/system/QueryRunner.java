
package me.heldplayer.SpAEconomy.system;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import javax.sql.DataSource;

public class QueryRunner {

    private volatile boolean pmdKnownBroken = false;
    protected final DataSource ds;

    public QueryRunner() {
        this.ds = null;
    }

    public QueryRunner(boolean pmdKnownBroken) {
        this.pmdKnownBroken = pmdKnownBroken;
        this.ds = null;
    }

    public QueryRunner(DataSource ds) {
        this.ds = ds;
    }

    public QueryRunner(DataSource ds, boolean pmdKnownBroken) {
        this.pmdKnownBroken = pmdKnownBroken;
        this.ds = ds;
    }

    public int[] batch(Connection conn, String sql, Object[][] params) throws SQLException {
        PreparedStatement stmt = null;
        int[] rows = null;
        try {
            stmt = this.prepareStatement(conn, sql);

            for (int i = 0; i < params.length; i++) {
                this.fillStatement(stmt, params[i]);
                stmt.addBatch();
            }
            rows = stmt.executeBatch();
        }
        catch (SQLException e) {
            this.rethrow(e, sql, (Object[]) params);
        }
        finally {
            this.close(stmt);
        }

        return rows;
    }

    public int[] batch(String sql, Object[][] params) throws SQLException {
        Connection conn = this.prepareConnection();
        try {
            int[] arrayOfInt = this.batch(conn, sql, params);
            return arrayOfInt;
        }
        finally {
            this.close(conn);
        }
    }

    public void fillStatement(PreparedStatement stmt, Object[] params) throws SQLException {
        if (params == null) {
            return;
        }

        ParameterMetaData pmd = null;
        if (!this.pmdKnownBroken) {
            pmd = stmt.getParameterMetaData();
            if (pmd.getParameterCount() < params.length) {
                throw new SQLException("Too many parameters: expected " + pmd.getParameterCount() + ", was given " + params.length);
            }
        }

        for (int i = 0; i < params.length; i++) {
            if (params[i] != null) {
                stmt.setObject(i + 1, params[i]);
            }
            else {
                int sqlType = 12;
                if (!this.pmdKnownBroken) {
                    try {
                        sqlType = pmd.getParameterType(i + 1);
                    }
                    catch (SQLException e) {
                        this.pmdKnownBroken = true;
                    }
                }
                stmt.setNull(i + 1, sqlType);
            }
        }
    }

    public void fillStatementWithBean(PreparedStatement stmt, Object bean, PropertyDescriptor[] properties) throws SQLException {
        Object[] params = new Object[properties.length];
        for (int i = 0; i < properties.length; i++) {
            PropertyDescriptor property = properties[i];
            Object value = null;
            Method method = property.getReadMethod();
            if (method == null) {
                throw new RuntimeException("No read method for bean property " + bean.getClass() + " " + property.getName());
            }
            try {
                value = method.invoke(bean, new Object[0]);
            }
            catch (InvocationTargetException e) {
                throw new RuntimeException("Couldn't invoke method: " + method, e);
            }
            catch (IllegalArgumentException e) {
                throw new RuntimeException("Couldn't invoke method with 0 arguments: " + method, e);
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException("Couldn't invoke method: " + method, e);
            }
            params[i] = value;
        }
        this.fillStatement(stmt, params);
    }

    public void fillStatementWithBean(PreparedStatement stmt, Object bean, String[] propertyNames) throws SQLException {
        PropertyDescriptor[] descriptors;
        try {
            descriptors = Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors();
        }
        catch (IntrospectionException e) {
            throw new RuntimeException("Couldn't introspect bean " + bean.getClass().toString(), e);
        }
        PropertyDescriptor[] sorted = new PropertyDescriptor[propertyNames.length];
        for (int i = 0; i < propertyNames.length; i++) {
            String propertyName = propertyNames[i];
            if (propertyName == null) {
                throw new NullPointerException("propertyName can't be null: " + i);
            }
            boolean found = false;
            for (int j = 0; j < descriptors.length; j++) {
                PropertyDescriptor descriptor = descriptors[j];
                if (propertyName.equals(descriptor.getName())) {
                    sorted[i] = descriptor;
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new RuntimeException("Couldn't find bean property: " + bean.getClass() + " " + propertyName);
            }
        }

        this.fillStatementWithBean(stmt, bean, sorted);
    }

    public DataSource getDataSource() {
        return this.ds;
    }

    protected PreparedStatement prepareStatement(Connection conn, String sql) throws SQLException {
        return conn.prepareStatement(sql);
    }

    protected Connection prepareConnection() throws SQLException {
        if (this.getDataSource() == null) {
            throw new SQLException("QueryRunner requires a DataSource to be invoked in this way, or a Connection should be passed in");
        }

        return this.getDataSource().getConnection();
    }

    @SuppressWarnings("unchecked")
    public <T> T query(Connection conn, String sql, ResultSetHandler<T> rsh, Object[] params) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Object result = null;
        try {
            sql = conn.nativeSQL(sql);
            stmt = this.prepareStatement(conn, sql);
            this.fillStatement(stmt, params);
            rs = this.wrap(stmt.executeQuery());
            result = rsh.handle(rs);
        }
        catch (SQLException e) {
            this.rethrow(e, sql, params);
        }
        finally {
            try {
                this.close(rs);
            }
            finally {
                this.close(stmt);
            }
        }

        return (T) result;
    }

    public <T> T query(Connection conn, String sql, ResultSetHandler<T> rsh) throws SQLException {
        return this.query(conn, sql, rsh, (Object[]) null);
    }

    @SuppressWarnings("unchecked")
    public <T> T query(String sql, ResultSetHandler<T> rsh, Object[] params) throws SQLException {
        Connection conn = this.prepareConnection();
        try {
            Object object = this.query(conn, sql, rsh, params);
            return (T) object;
        }
        finally {
            this.close(conn);
        }
    }

    public <T> T query(String sql, ResultSetHandler<T> rsh) throws SQLException {
        return this.query(sql, rsh, (Object[]) null);
    }

    protected void rethrow(SQLException cause, String sql, Object[] params) throws SQLException {
        String causeMessage = cause.getMessage();
        if (causeMessage == null) {
            causeMessage = "";
        }
        StringBuffer msg = new StringBuffer(causeMessage);

        msg.append(" Query: ");
        msg.append(sql);
        msg.append(" Parameters: ");

        if (params == null) {
            msg.append("[]");
        }
        else {
            msg.append(Arrays.deepToString(params));
        }

        SQLException e = new SQLException(msg.toString(), cause.getSQLState(), cause.getErrorCode());

        e.setNextException(cause);

        throw e;
    }

    public int update(Connection conn, String sql) throws SQLException {
        return this.update(conn, sql, (Object[]) null);
    }

    public int update(Connection conn, String sql, Object param) throws SQLException {
        return this.update(conn, sql, new Object[] { param });
    }

    public int update(Connection conn, String sql, Object[] params) throws SQLException {
        PreparedStatement stmt = null;
        int rows = 0;
        try {
            stmt = this.prepareStatement(conn, sql);
            this.fillStatement(stmt, params);
            rows = stmt.executeUpdate();
        }
        catch (SQLException e) {
            this.rethrow(e, sql, params);
        }
        finally {
            this.close(stmt);
        }

        return rows;
    }

    public int update(String sql) throws SQLException {
        return this.update(sql, (Object[]) null);
    }

    public int update(String sql, Object param) throws SQLException {
        return this.update(sql, new Object[] { param });
    }

    public int update(String sql, Object[] params) throws SQLException {
        Connection conn = this.prepareConnection();
        try {
            int i = this.update(conn, sql, params);
            return i;
        }
        finally {
            this.close(conn);
        }
    }

    protected ResultSet wrap(ResultSet rs) {
        return rs;
    }

    protected void close(Connection conn) throws SQLException {
        DbUtils.close(conn);
    }

    protected void close(Statement stmt) throws SQLException {
        DbUtils.close(stmt);
    }

    protected void close(ResultSet rs) throws SQLException {
        DbUtils.close(rs);
    }
}
