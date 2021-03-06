package com.chinaops.web.edesktop;


import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.utils.ad.ADInfo;

/**
 * @Description:获取AD域用户
 * @author moonxy
 * @date 2018-05-14
 */
public class ADUtils {
    public static void main(String[] args) {
        Properties env = new Properties();
        //使用UPN格式：User@domain或SamAccountName格式：domain\\User
        String adminName = "administrator@pjy.com";
        String adminPassword = "supcon@1304";//password
        env.put(Context.PROVIDER_URL, ADInfo.ldapURL636);
		env.put("java.naming.ldap.factory.socket", ADInfo.factorysocket);

        env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
        
        env.put(Context.SECURITY_AUTHENTICATION, "simple");//LDAP访问安全级别："none","simple","strong"
        env.put(Context.SECURITY_PRINCIPAL, adminName);// AD User
        env.put(Context.SECURITY_CREDENTIALS, adminPassword);// AD Password 
       // env.put(Context.PROVIDER_URL, ldapURL);// LDAP工厂类
        
        try {
            LdapContext ctx = new InitialLdapContext(env, null);
            //搜索控制器
            SearchControls searchCtls = new SearchControls();
            //创建搜索控制器 
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            //LDAP搜索过滤器类，此处只获取AD域用户，所以条件为用户user或者person均可
            //(&(objectCategory=person)(objectClass=user)(name=*))
            String searchFilter = "objectClass=user";
            //AD域节点结构
            String searchBase = "CN=lanzhu,CN=Users,DC=pjy,DC=com";
            
            String returnedAtts[] = { "url", "employeeID",  "mail",
                    "name", "userPrincipalName", "unicodePwd"}; // 定制返回属性
            searchCtls.setReturningAttributes(returnedAtts);
            
            
            NamingEnumeration<SearchResult> answer = ctx.search(searchBase, searchFilter,searchCtls);
            
            while (answer.hasMoreElements()) {
                SearchResult sr = (SearchResult) answer.next();
                System.out.println("<<<::[" + sr.getName()+"]::>>>>");//返回格式一般是CN=xxxx,OU=xxxx
                Attributes Attrs = sr.getAttributes();//得到符合条件的属性集  
                if (Attrs != null) {
                    for (NamingEnumeration ne = Attrs.getAll(); ne.hasMore();) {
                        Attribute Attr = (Attribute) ne.next();//得到下一个属性
                        System.out.print(Attr.getID().toString() + ":");
                        //读取属性值
                        for (NamingEnumeration e = Attr.getAll(); e.hasMore();) {
                            String userInfo =  e.next().toString();
                            System.out.print(userInfo);
                        }
                        System.out.println("");
                    }
                }
            }
            ctx.close();
        }catch (NamingException e) {
            e.printStackTrace();
            System.err.println("Problem searching directory: " + e);
        }
    }
}