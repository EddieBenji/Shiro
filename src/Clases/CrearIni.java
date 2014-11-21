/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.Ini;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.subject.Subject;

/**
 *
 * @author Javier Mijangos
 */
public class CrearIni {
    
    DefaultSecurityManager sm=new DefaultSecurityManager();
        Ini ini=new Ini();
    private void crearIni(){
        Ini.Section usuarios=ini.addSection(IniRealm.USERS_SECTION_NAME);
        Ini.Section roles = ini.addSection(IniRealm.ROLES_SECTION_NAME);
        
        roles.put("Vendedor", "*");
        roles.put("Conductor", "*");
        
        usuarios.put("Pepe","clave, Vendedor");
        usuarios.put("Lola","123, Conductor");            
        sm.setRealm(new IniRealm(ini));
        SecurityUtils.setSecurityManager(sm);
    }
    
    public void iniciarSesion(){
        crearIni();
        Subject currentUser = SecurityUtils.getSubject();
        if ( !currentUser.isAuthenticated() ) {
            UsernamePasswordToken token = new UsernamePasswordToken("Pepe", "clave" );
            //UsernamePasswordToken token = new UsernamePasswordToken("Lola", "123" );
            //UsernamePasswordToken token = new UsernamePasswordToken("nombre", "1234" );

            token.setRememberMe(true);
            try {
                currentUser.login(token);
            } catch (UnknownAccountException uae) {
                System.out.println("No hay usuario con el nombre " + token.getPrincipal() );
            } catch ( IncorrectCredentialsException ice ) {
                System.out.println("Password para la cuenta " + token.getPrincipal() + " es incorrecto");
            }
        }     
                  
        if(currentUser.isAuthenticated()){
            System.out.println("User [" + currentUser.getPrincipal() + "] logged in successfully.");
        }else{
            System.out.println("logged in fail.");
        }
        
        if ( currentUser.hasRole( "Vendedor" ) ) {
            System.out.println("Puede vender" );
        } else {
            System.out.println( "No puede vender" );
        }
 
         if ( currentUser.hasRole( "Conductor" ) ) {
            System.out.println("Maneja" );
        } else {
            System.out.println( "No maneja" );
        }
    }
    
    public static void main(String[] args) {
        CrearIni ejemplo = new CrearIni();
        ejemplo.iniciarSesion();
    }
   
}
