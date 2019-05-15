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
 * @Description:对AD域用户的增删改查操作
 * @author moonxy
 * @date 2018-05-15
 */
public class ADUserUtils {
	DirContext dc = null;
	String root = "DC=pjy,DC=com"; // LDAP的根节点的DC

	/**
	 * @Description:程序入口
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
	 * 初始化
	 */
	public ADUserUtils() {
		super();
		init();
	}

	/**
	 * @Description:初始化AD域服务连接
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

		env.put(Context.SECURITY_AUTHENTICATION, "simple");// LDAP访问安全级别："none","simple","strong"
		env.put(Context.SECURITY_PRINCIPAL, adminName);
		env.put(Context.SECURITY_CREDENTIALS, adminPassword);
		env.put(Context.PROVIDER_URL, ldapURL);
		try {
			dc = new InitialLdapContext(env, null);
			System.out.println("AD域服务连接认证成功");
		} catch (Exception e) {
			System.out.println("AD域服务连接认证失败");
			e.printStackTrace();
		}
	}

	/**
	 * @Description:关闭AD域服务连接
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
	 * @Description:搜索指定节点下的所有AD域用户
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
	 * @Description:指定搜索节点搜索指定域用户
	 * @author moonxy
	 * @date 2018-05-15
	 */
	public SearchResult searchByUserName(String searchBase, String userName) {
		SearchControls searchCtls = new SearchControls();
		searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String searchFilter = "sAMAccountName=" + userName;
		String returnedAtts[] = { "memberOf" }; // 定制返回属性
		searchCtls.setReturningAttributes(returnedAtts); // 设置返回属性集
		try {
			NamingEnumeration<SearchResult> answer = dc.search(searchBase, searchFilter, searchCtls);
			return answer.next();
		} catch (Exception e) {
			System.err.println("指定搜索节点搜索指定域用户失败");
			e.printStackTrace();
		}
		return null;
	}
}