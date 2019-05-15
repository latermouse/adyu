package com.chinaops.web.edesktop;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;

/**
 * @Description:��AD���û�����ɾ�Ĳ����
 * @author moonxy
 * @date 2018-05-15
 */
public class ADUserUtils {
	DirContext dc = null;
	String root = "DC=pjy,DC=com"; // LDAP�ĸ��ڵ��DC

	/**
	 * @Description:�������
	 * @author moonxy
	 * @date 2018-05-15
	 */
	public static void main(String[] args) {
		ADUserUtils utils = new ADUserUtils();

		// utils.add("JimGreen");

		SearchResult sr = utils.searchByUserName(utils.root, "xiaopan");

		System.out.println(sr);
		// System.out.println(sr.getName());
		//
		// utils.searchInformation(sr.getName());

		utils.close();
	}

	/**
	 * ��ʼ��
	 */
	public ADUserUtils() {
		super();
		init();
	}

	/**
	 * @Description:��ʼ��AD���������
	 * @author moonxy
	 * @date 2018-05-15
	 */
	public void init() {
		Properties env = new Properties();
		String adminName = "administrator@pjy.com";
		String adminPassword = "supcon@1304";// password
		// String ldapURL = "LDAP://10.10.51.222:636";// ip:port
		String ldapURL = "LDAP://10.10.51.222:389";// ip:port
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		// env.put("java.naming.ldap.factory.socket",
		// "com.chinaops.web.edesktop.DummySSLSocketFactory");

		env.put(Context.SECURITY_AUTHENTICATION, "simple");// LDAP���ʰ�ȫ����"none","simple","strong"
		env.put(Context.SECURITY_PRINCIPAL, adminName);
		env.put(Context.SECURITY_CREDENTIALS, adminPassword);
		env.put(Context.PROVIDER_URL, ldapURL);
		try {
			dc = new InitialLdapContext(env, null);
			System.out.println("AD�����������֤�ɹ�");
		} catch (Exception e) {
			System.out.println("AD�����������֤ʧ��");
			e.printStackTrace();
		}
	}

	/**
	 * @Description:�ر�AD���������
	 * @author moonxy
	 * @date 2018-05-15
	 */
	public void close() {
		if (dc != null) {
			try {
				dc.close();
			} catch (NamingException e) {
				System.out.println("NamingException in close():" + e);
			}
		}
	}

	/**
	 * @Description:����ָ���ڵ��µ�����AD���û�
	 * @author moonxy
	 * @date 2018-05-15
	 */
	public void searchInformation(String searchBase) {
		try {
			SearchControls searchCtls = new SearchControls();
			searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String searchFilter = "objectClass=user";
			String returnedAtts[] = { "memberOf" };
			searchCtls.setReturningAttributes(returnedAtts);
			NamingEnumeration<SearchResult> answer = dc.search(searchBase, searchFilter, searchCtls);
			while (answer.hasMoreElements()) {
				SearchResult sr = (SearchResult) answer.next();
				System.out.println("<<<::[" + sr.getName() + "]::>>>>");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Description:ָ�������ڵ�����ָ�����û�
	 * @author moonxy
	 * @date 2018-05-15
	 */
	public SearchResult searchByUserName(String searchBase, String userName) {
		SearchControls searchCtls = new SearchControls();
		searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String searchFilter = "sAMAccountName=" + userName;
		String returnedAtts[] = { "memberOf" }; // ���Ʒ�������
		searchCtls.setReturningAttributes(returnedAtts); // ���÷������Լ�
		try {
			NamingEnumeration<SearchResult> answer = dc.search(searchBase, searchFilter, searchCtls);
			return answer.next();
		} catch (Exception e) {
			System.err.println("ָ�������ڵ�����ָ�����û�ʧ��");
			e.printStackTrace();
		}
		return null;
	}
}