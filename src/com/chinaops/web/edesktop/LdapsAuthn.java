package com.chinaops.web.edesktop;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.Hashtable;
 
public class LdapsAuthn {
    /**
     * ��������ַ
     */
    public static final String server = " ";
    /**
     * �˿�
     */
    public static final String port = "636";
    /**
     * ��¼��
     */
    public static final String admin = " ";
    /**
     * ����
     */
    public static final String adminPass = " ";
    /**
     *����֤���û����ź�����
     */
    public static final String testUser = " ";
    public static final String testPassword = " ";
 
    public static final String baseDN = " ";
 
    public static void main(String args[]) {
        /**
         *���ӷ�����������֤����ȷ���true�����󷵻�false
         */
        boolean  verify = connect(server, port, admin, adminPass, testUser, baseDN);
        System.out.println(verify);
    }
 
    public static boolean connect(String server, String port, String user, String passwd, String testUser, String baseDN) {
        boolean result = false;
        InitialDirContext ctx = null;
        InitialDirContext context = null;
        /**
         * ���ӵķ�������ַ����ƴ��
         */
        String ldapURL = "ldap://" + server + ":" + port;
        /**
         * ������������
         */
        Hashtable<String, String> env =new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, user);
        env.put(Context.SECURITY_CREDENTIALS, passwd);
        env.put(Context.PROVIDER_URL, ldapURL);
        env.put(Context.REFERRAL,"ignore");
        env.put(Context.SECURITY_PROTOCOL,"ssl");
        env.put("java.naming.ldap.factory.socket", "����.DummySSLSocketFactory");
 
        try {
            ctx = new InitialDirContext(env);
            SearchControls searchCtls = new SearchControls();
            /**
             * ����Ϊ������ΧΪ����Ŀ¼
             */
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            /**
             * ����
             */
            NamingEnumeration<?> results = ctx.search(baseDN, "sAMAccountName=" + testUser, searchCtls);
            /**
             * �Ñ���DN
             */
            String userDN = null;
            /**
             * ȡ���������
             */
            while (results.hasMoreElements()) {
                SearchResult sr = (SearchResult) results.next();
                userDN = sr.getName();
                System.out.println(userDN);
            }
            /**
             * ���ô���֤���û���������
             */
            env.put(Context.SECURITY_PRINCIPAL, userDN + "," + baseDN);
            env.put(Context.SECURITY_CREDENTIALS, testPassword);
 
            context = new InitialDirContext(env);
 
            System.out.println("������ȷ");
            result = true;
        } catch (NamingException e) {
            e.printStackTrace();
        } finally {
            /**
             * �ر���Դ
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
