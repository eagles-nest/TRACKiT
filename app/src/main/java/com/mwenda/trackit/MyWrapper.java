package com.mwenda.trackit;

import java.sql.SQLException;
import java.sql.Wrapper;

/**
 * Created by NDANU on 10/25/2018.
 */

public class MyWrapper implements Wrapper {
    public String method;
    public String username;
    public String email;
    public String password;
    public String ownPhone;
    public String gsmPhone;
    public String result;

    MyWrapper(String method,String email,String username,String password,String ownPhone,String gsmPhone,String result){
        this.method=method;
        this.email=email;
        this.username=username;
        this.password=password;
        this.ownPhone=ownPhone;
        this.gsmPhone=gsmPhone;
        this.result=result;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
