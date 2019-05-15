package com.chinaops.web.edesktop;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.Hashtable;
 
public class LdapsAuthn {
    /**
     * 服务器地址
     */
    public static final String server = " ";
    /**
     * 端口
     */
    public static final String port = "636";
    /**
     * 登录名
     */
    public static final String admin = " ";
    /**
     * 密码
     */
    public static final String adminPass = " ";
    /**
     *待验证的用户工号和密码
     */
    public static final String testUser = " ";
    public static final String testPassword = " ";
 
    public static final String baseDN = " ";
 
    public static void main(String args[]) {
        /**
         *连接服务器进行验证，正确输出true，错误返回false
         */
        boolean  verify = connect(server, port, admin, adminPass, testUser, baseDN);
        System.out.println(verify);
    }
 
    public static boolean connect(String server, String port, String user, String passwd, String testUser, String baseDN) {
        boolean result = false;
        InitialDirContext ctx = null;
        InitialDirContext context = null;
        /**
         * 连接的服务器地址进行拼接
         */
        String ldapURL = "ldap://" + server + ":" + port;
        /**
         * 配置连接属性
         */
        Hashtable<String, String> env =new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, user);
        env.put(Context.SECURITY_CREDENTIALS, passwd);
        env.put(Context.PROVIDER_URL, ldapURL);
        env.put(Context.REFERRAL,"ignore");
        env.put(Context.SECURITY_PROTOCOL,"ssl");
        env.put("java.naming.ldap.factory.socket", "包名.DummySSLSocketFactory");
 
        try {
            ctx = new InitialDirContext(env);
            SearchControls searchCtls = new SearchControls();
            /**
             * 设置为搜索范围为整个目录
             */
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            /**
             * 搜索
             */
            NamingEnumeration<?> results = ctx.search(baseDN, "sAMAccountName=" + testUser, searchCtls);
            /**
             * 用戶的DN
             */
            String userDN = null;
            /**
             * 取出搜索结果
             */
            while (results.hasMoreElements()) {
                SearchResult sr = (SearchResult) results.next();
                userDN = sr.getName();
                System.out.println(userDN);
            }
            /**
             * 配置待验证的用户名和密码
             */
            env.put(Context.SECURITY_PRINCIPAL, userDN + "," + baseDN);
            env.put(Context.SECURITY_CREDENTIALS, testPassword);
 
            context = new InitialDirContext(env);
 
            System.out.println("密码正确");
            result = true;
        } catch (NamingException e) {
            e.printStackTrace();
        } finally {
            /**
             * 关闭资源
             */
            if(ctx != null){
                try {
                    ctx.close();
                } catch (NamingException e){
                    e.printStackTrace();
                }
            }
            if(context != null){
                try {
                    context.close();
                } catch (NamingException e){
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
