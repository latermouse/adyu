package org.utils.ad;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

/**
 * 
 * @author panjianyu 专用修改密码
 *
 */
public class ModifyPwd {
	LdapContext dc = null;

	public static void main(String[] args) {
		ModifyPwd m = new ModifyPwd();
		m.updatePWD("cn=小潘6,OU=信息部,OU=中控技术,DC=pjy,DC=com", "44444444");
	}

	public void updatePWD(String dn, String pwd) {
		init();
		try {
			String newQuotedPassword = "\"" + pwd + "\"";
			byte[] newUnicodePassword = newQuotedPassword.getBytes("UTF-16LE");
			ModificationItem[] mods = new ModificationItem[1];
			mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
					new BasicAttribute("unicodePwd", newUnicodePassword));
			dc.modifyAttributes(dn, mods);
			System.out.println("修改密码成功！");
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	// 初始化
	public void init() {
		Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY, ADInfo.INITIAL_CONTEXT_FACTORY);
		env.put(Context.SECURITY_AUTHENTICATION, ADInfo.SECURITY_AUTHENTICATION);
		env.put(Context.SECURITY_PRINCIPAL, ADInfo.adminName + ADInfo.adminNamelast);
		env.put(Context.SECURITY_CREDENTIALS, ADInfo.adminPassword);
		env.put(Context.PROVIDER_URL, ADInfo.ldapURL636);
		env.put("java.naming.ldap.factory.socket", ADInfo.factorysocket);
		System.out.println(111);
		try {
			dc = new InitialLdapContext(env, null);
			
			System.out.println("AD域服务连接认证成功");
		} catch (Exception e) {
			System.out.println("AD域服务连接认证失败");
			e.printStackTrace();
		}
	}

	public void close() {
		if (dc != null) {
			try {
				dc.close();
			} catch (NamingException e) {
				System.out.println("NamingException in close():" + e);
			}
		}
	}

}
