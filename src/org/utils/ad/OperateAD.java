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
 * @author panjianyu 用来对域进行操作，不包括密码修改， 主要功能1，创建账户 2，修改属性
 *
 */
public class OperateAD {
	LdapContext dc = null;

	public static void main(String[] args) {
		OperateAD o = new OperateAD();
		o.add("小潘8", "cn=小潘8,OU=信息化部,OU=中控技术,DC=pjy,DC=com", "xiaopan8");
		// o.modifyInformation("CN=小潘,OU=信息部,OU=中控技术,DC=pjy,DC=com",
		// "userWorkstations", "xiaopan,supconad");
	}

	/*
	 * dn为账户位置，newUserName用户名,loginName 登录名英文
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
			attrs.put("userWorkstations", newUserName + "," + loginName + "," + "supconad");// 登录工作站

			dc.createSubcontext(dn, attrs);
			System.out.println("新增AD域用户成功:" + newUserName);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("新增AD域用户失败:" + newUserName);
		}
		close();
	}

	/*
	 * dn为账户位置，fieldValue属性值,fieldname属性名，
	 * 
	 */
	public boolean modifyInformation(String dn, String fieldname, String fieldValue) {
		try {
			ModificationItem[] mods = new ModificationItem[1];
			// 修改属性
			Attribute attr0 = new BasicAttribute(fieldname, fieldValue);
			mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, attr0);// 新增属性
			// mods[0] = new
			// ModificationItem(DirContext.REMOVE_ATTRIBUTE,attr0);//删除属性
			// mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
			// attr0);// 覆盖属性
			dc.modifyAttributes(dn, mods);
			System.out.println("修改AD域用户属性成功");
			return true;
		} catch (Exception e) {
			System.err.println("修改AD域用户属性失败");
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
	 * @Description:搜索指定节点下的所有AD域用户
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

	// 初始化
	public void init() {
		Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY, ADInfo.INITIAL_CONTEXT_FACTORY);
		env.put(Context.SECURITY_AUTHENTICATION, ADInfo.SECURITY_AUTHENTICATION);
		env.put(Context.SECURITY_PRINCIPAL, ADInfo.adminName + ADInfo.adminNamelast);
		env.put(Context.SECURITY_CREDENTIALS, ADInfo.adminPassword);
		env.put(Context.PROVIDER_URL, ADInfo.ldapURL389);
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
