package org.utils.ad;

import javax.naming.Context;
/****
 * 
 * @author panjianyu
 *连接类
 */
public class ADInfo {
	public static final String adminName = "administrator";
	public static final String adminNamelast = "@pjy.com";
	public static final String adminPassword = "supcon@1304";// password
	public static final String ldapURL389 = "LDAP://1IP:389";// 用于普通操作
	public static final String INITIAL_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
	public static final String SECURITY_AUTHENTICATION = "simple";// LDAP访问安全级别："none","simple","strong"
	public static final String ldapURL636 = "LDAP://IP:636";// 用于密码操作，端口不一样
	public static final String factorysocket = "org.utils.ad.DummySSLSocketFactory";// 用于密码操作

}
