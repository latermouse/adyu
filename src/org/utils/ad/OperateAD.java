package org.utils.ad;

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
import javax.naming.ldap.LdapContext;

/**
 * 
 * @author panjianyu ����������в����������������޸ģ� ��Ҫ����1�������˻� 2���޸�����
 *
 */
public class OperateAD {
	LdapContext dc = null;

	public static void main(String[] args) {
		OperateAD o = new OperateAD();
		o.add("С��8", "cn=С��8,OU=��Ϣ����,OU=�пؼ���,DC=pjy,DC=com", "xiaopan8");
		// o.modifyInformation("CN=С��,OU=��Ϣ��,OU=�пؼ���,DC=pjy,DC=com",
		// "userWorkstations", "xiaopan,supconad");
	}

	/*
	 * dnΪ�˻�λ�ã�newUserName�û���,loginName ��¼��Ӣ��
	 * 
	 */
	public void add(String newUserName, String dn, String loginName) {
		init();
		try {
			Attributes attrs = new BasicAttributes(true);
			attrs.put("objectClass", "user");
			attrs.put("sn", newUserName);
			attrs.put("name", newUserName);
			attrs.put("displayName", newUserName);
			attrs.put("sAMAccountName", loginName);
			attrs.put("userPrincipalName", loginName + ADInfo.adminNamelast);
			attrs.put("userWorkstations", newUserName + "," + loginName + "," + "supconad");// ��¼����վ

			dc.createSubcontext(dn, attrs);
			System.out.println("����AD���û��ɹ�:" + newUserName);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("����AD���û�ʧ��:" + newUserName);
		}
		close();
	}

	/*
	 * dnΪ�˻�λ�ã�fieldValue����ֵ,fieldname��������
	 * 
	 */
	public boolean modifyInformation(String dn, String fieldname, String fieldValue) {
		try {
			ModificationItem[] mods = new ModificationItem[1];
			// �޸�����
			Attribute attr0 = new BasicAttribute(fieldname, fieldValue);
			mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, attr0);// ��������
			// mods[0] = new
			// ModificationItem(DirContext.REMOVE_ATTRIBUTE,attr0);//ɾ������
			// mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
			// attr0);// ��������
			dc.modifyAttributes(dn, mods);
			System.out.println("�޸�AD���û����Գɹ�");
			return true;
		} catch (Exception e) {
			System.err.println("�޸�AD���û�����ʧ��");
			e.printStackTrace();
			return false;
		}
	}
	/*
	 * 
	 */
	public boolean changeOU(String olduserDN, String newOU) throws NamingException  {
		LdapContext ctx =null;
		try{
    		String[] user=olduserDN.split(",");
    		String newDN=user[0]+","+newOU+user[user.length-2]+","+user[user.length-1];
    		dc.rename(olduserDN,newDN);
       	 return true;
        	}catch(Exception e){
				e.printStackTrace();
			} finally {
        	if (ctx != null) {
                	ctx.close();
            	ctx = null;
        	}
        }
	    return false;
	}


	/**
	 * @Description:����ָ���ڵ��µ�����AD���û�
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

	// ��ʼ��
	public void init() {
		Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY, ADInfo.INITIAL_CONTEXT_FACTORY);
		env.put(Context.SECURITY_AUTHENTICATION, ADInfo.SECURITY_AUTHENTICATION);
		env.put(Context.SECURITY_PRINCIPAL, ADInfo.adminName + ADInfo.adminNamelast);
		env.put(Context.SECURITY_CREDENTIALS, ADInfo.adminPassword);
		env.put(Context.PROVIDER_URL, ADInfo.ldapURL389);
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
