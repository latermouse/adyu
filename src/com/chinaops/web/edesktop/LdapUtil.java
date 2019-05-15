package com.chinaops.web.edesktop;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

public class LdapUtil {

	// private static final ResourceBundle bundle =
	// ResourceBundle.getBundle("ldap");
	private static final String LDAP_USERNAME = "administrator@pjy";
	private static final String LDAP_PASSWORD = "supcon@1304";
	private static final String LDAP_IP = "10.10.51.222";
	private static final String LDAP_PORT = "389";
	private static final String LDAP_ROOT = "com";
	private static final String LDAP_OBJCLASS_NAME = "objectClass=user";
	private static final String LDAP_ERROR_LOG_PATH = "";

	public static void main(String[] args) {
		HashMap hash = new HashMap();
		
		hash.put("CN", "panjianyu");
		hash.put("CN", "Users");
		hash.put("DC", "pjy");
		hash.put("DC", "com");

		save(hash);
		// LdapUtil.delete("loginname=wjm");
	}

	/**
	 * LDAP保存一条数据方法，字段名和值由hash来传入，hash的第一个键值对必须是dn
	 * 
	 * @param hash
	 * @return
	 */
	public static HashMap save(HashMap hash) {
		Hashtable env = new Hashtable();

		String adminName = "administrator@pjy.com";
		String adminPassword = "supcon@1304";// password
		String ldapURL = "LDAP://10.10.51.222:389";// ip:port

		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.SECURITY_AUTHENTICATION, "simple");// LDAP访问安全级别："none","simple","strong"
		env.put(Context.SECURITY_PRINCIPAL, adminName);// AD User
		env.put(Context.SECURITY_CREDENTIALS, adminPassword);// AD Password
		env.put(Context.PROVIDER_URL, ldapURL);// LDAP工厂类

		HashMap ret = new HashMap();
		ArrayList array = new ArrayList();
		DirContext ctx = null;
		String dn = "";
		StringBuffer errors = new StringBuffer();
		try {
			ctx = new InitialDirContext(env);
			System.out.println("ldap连接成功");
			BasicAttributes attrsbu = new BasicAttributes();
			BasicAttribute objclassSet = new BasicAttribute("objectclass");
			objclassSet.add(LDAP_OBJCLASS_NAME);
			attrsbu.put(objclassSet);
			Set s = hash.keySet();
			Iterator i = s.iterator();
			boolean flag = false;
			if (i.hasNext()) {
				Object obj = i.next();
				dn = obj.toString() + "=" + hash.get(obj);
				flag = true;
			}
			while (i.hasNext()) {
				Object obj = i.next();
				attrsbu.put(obj.toString(), hash.get(obj));
			}
			if (flag) {
				ctx.createSubcontext(dn, attrsbu);
			} else {
				errors.append("没有传入DN");
				array.add("没有传入DN");
			}
		} catch (javax.naming.AuthenticationException e) {
			errors.append(e.getMessage());
			e.printStackTrace();
			array.add(e.getMessage());
		} catch (Exception e) {
			errors.append("LDAP连接出错：" + e.getMessage());
			e.printStackTrace();
			array.add("LDAP连接出错：" + e.getMessage());
		}
		if (ctx != null) {
			try {
				ctx.close();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
		if (!errors.toString().equals("")) {
			File folder = new File(LDAP_ERROR_LOG_PATH);
			/*
			 * if (folder.exists() || folder.mkdirs()) { File file = new
			 * File(LDAP_ERROR_LOG_PATH + "\\" + TimeUtil.getCurrentDateTime("-
			 * ", "-") + ".log"); FileWriter fw = null; try { fw = new
			 * FileWriter(file); fw.write(dn + " -----> " + errors.toString());
			 * } catch (IOException e) { e.printStackTrace(); } finally { try {
			 * fw.close(); } catch (IOException e) { e.printStackTrace(); } } }
			 */
		}
		ret.put(dn, array);
		return ret;
	}

	public static HashMap delete(String dn) {
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://" + LDAP_IP + ":" + LDAP_PORT + "/" + LDAP_ROOT);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, "cn=" + LDAP_USERNAME + "," + LDAP_ROOT);
		env.put(Context.SECURITY_CREDENTIALS, LDAP_PASSWORD);
		HashMap ret = new HashMap();
		ArrayList array = new ArrayList();
		DirContext ctx = null;
		StringBuffer errors = new StringBuffer();
		try {
			ctx = new InitialDirContext(env);
			ctx.destroySubcontext(dn);
		} catch (javax.naming.AuthenticationException e) {
			errors.append(e.getMessage());
			e.printStackTrace();
			array.add(e.getMessage());
		} catch (Exception e) {
			errors.append("LDAP连接出错：" + e.getMessage());
			e.printStackTrace();
			array.add("LDAP连接出错：" + e.getMessage());
		}
		if (ctx != null) {
			try {
				ctx.close();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
		if (!errors.toString().equals("")) {
			File folder = new File(LDAP_ERROR_LOG_PATH);
			if (folder.exists() || folder.mkdirs()) {
				/*
				 * File file = new File(LDAP_ERROR_LOG_PATH +
				 * "\\" + TimeUtil.getCurrentDateTime("-", "-") + ".log");
				 * FileWriter fw = null; try { fw = new FileWriter(file);
				 * fw.write(dn + " -----> " + errors.toString()); } catch
				 * (IOException e) { e.printStackTrace(); } finally { try {
				 * fw.close(); } catch (IOException e) { e.printStackTrace(); }
				 * }
				 */
			}
		}
		ret.put(dn, array);
		return ret;
	}

}