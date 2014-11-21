/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package shiro;

import ejemplo.Quickstart;
import org.apache.logging.log4j.LogManager;
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
    
    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger(CrearIni.class);
    
    
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
                log.info("No hay usuario con el nombre " + token.getPrincipal() );
                
            } catch ( IncorrectCredentialsException ice ) {
                log.info("Password para la cuenta " + token.getPrincipal() + " es incorrecto");
            }
        }     
                  
        if(currentUser.isAuthenticated()){
            log.info("User [" + currentUser.getPrincipal() + "] logged in successfully.");
            
        }else{
            log.info("logged in fail.");
            
        }
        
        if ( currentUser.hasRole( "Vendedor" ) ) {
            log.info("Puede vender");
            
        } else {
            log.info("No puede vender" );
            
        }
 
         if ( currentUser.hasRole( "Conductor" ) ) {
             log.info("Maneja");
            
        } else {
             log.info( "No maneja" );
            
        }
    }
    
    public static void main(String[] args) {
        CrearIni ejemplo = new CrearIni();
        ejemplo.iniciarSesion();
    }
   
}
