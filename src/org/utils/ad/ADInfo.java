package org.utils.ad;

import javax.naming.Context;
/****
 * 
 * @author panjianyu
 *������
 */
public class ADInfo {
	public static final String adminName = "administrator";
	public static final String adminNamelast = "@pjy.com";
	public static final String adminPassword = "supcon@1304";// password
	public static final String ldapURL389 = "LDAP://1IP:389";// ������ͨ����
	public static final String INITIAL_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
	public static final String SECURITY_AUTHENTICATION = "simple";// LDAP���ʰ�ȫ����"none","simple","strong"
	public static final String ldapURL636 = "LDAP://IP:636";// ��������������˿ڲ�һ��
	public static final String factorysocket = "org.utils.ad.DummySSLSocketFactory";// �����������

}
