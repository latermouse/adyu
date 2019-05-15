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
 * @author panjianyu ר���޸�����
 *
 */
public class ModifyPwd {
	LdapContext dc = null;

	public static void main(String[] args) {
		ModifyPwd m = new ModifyPwd();
		m.updatePWD("cn=С��6,OU=��Ϣ��,OU=�пؼ���,DC=pjy,DC=com", "44444444");
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
			System.out.println("�޸�����ɹ���");
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	// ��ʼ��
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
			
			System.out.println("AD�����������֤�ɹ�");
		} catch (Exception e) {
			System.out.println("AD�����������֤ʧ��");
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
