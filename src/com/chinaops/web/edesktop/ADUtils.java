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
 * @Description:��ȡAD���û�
 * @author moonxy
 * @date 2018-05-14
 */
public class ADUtils {
    public static void main(String[] args) {
        Properties env = new Properties();
        //ʹ��UPN��ʽ��User@domain��SamAccountName��ʽ��domain\\User
        String adminName = "administrator@pjy.com";
        String adminPassword = "supcon@1304";//password
        env.put(Context.PROVIDER_URL, ADInfo.ldapURL636);
		env.put("java.naming.ldap.factory.socket", ADInfo.factorysocket);

        env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
        
        env.put(Context.SECURITY_AUTHENTICATION, "simple");//LDAP���ʰ�ȫ����"none","simple","strong"
        env.put(Context.SECURITY_PRINCIPAL, adminName);// AD User
        env.put(Context.SECURITY_CREDENTIALS, adminPassword);// AD Password 
       // env.put(Context.PROVIDER_URL, ldapURL);// LDAP������
        
        try {
            LdapContext ctx = new InitialLdapContext(env, null);
            //����������
            SearchControls searchCtls = new SearchControls();
            //�������������� 
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            //LDAP�����������࣬�˴�ֻ��ȡAD���û�����������Ϊ�û�user����person����
            //(&(objectCategory=person)(objectClass=user)(name=*))
            String searchFilter = "objectClass=user";
            //AD��ڵ�ṹ
            String searchBase = "CN=lanzhu,CN=Users,DC=pjy,DC=com";
            
            String returnedAtts[] = { "url", "employeeID",  "mail",
                    "name", "userPrincipalName", "unicodePwd"}; // ���Ʒ�������
            searchCtls.setReturningAttributes(returnedAtts);
            
            
            NamingEnumeration<SearchResult> answer = ctx.search(searchBase, searchFilter,searchCtls);
            
            while (answer.hasMoreElements()) {
                SearchResult sr = (SearchResult) answer.next();
                System.out.println("<<<::[" + sr.getName()+"]::>>>>");//���ظ�ʽһ����CN=xxxx,OU=xxxx
                Attributes Attrs = sr.getAttributes();//�õ��������������Լ�  
                if (Attrs != null) {
                    for (NamingEnumeration ne = Attrs.getAll(); ne.hasMore();) {
                        Attribute Attr = (Attribute) ne.next();//�õ���һ������
                        System.out.print(Attr.getID().toString() + ":");
                        //��ȡ����ֵ
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