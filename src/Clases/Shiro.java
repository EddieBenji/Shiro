package Clases;

import java.util.Scanner;
import java.util.Scanner;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.Ini;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.subject.Subject;
/**
 *
 * @author Lalo
 */
public class Shiro {
    /**
     * Son 3 cosas lo que necesitamos:
     *
     *   - Un módulo para inicio de sesión 
     *   - Un módulo para encriptar/descrncriptar las contraseñas 
     *   - Otro para cargar los usuario en caché.
     *
     * Por lo que me han dicho otros equipos es que para cada sesión habrá que
     * tener una caché diferente, ya que en cada sesión pueden agregar
     * candidatos, pero no todos podrán verlo. Sin embargo, lo que podríamos
     * hacer es que todos los candidatos se guarden en la caché, pero marcar qué
     * candidatos fueron creados por cuáles usuarios, así cuando quiera ver los
     * candidatos que creó primero verificaríamos que el candidato haya sido
     * creado por dicho usuario.
     *
     *
     */
    
    /** El SecurityUtils, que es el que maneja todo en Shiro, necesita un
     SecurityManager, en este caso usaré el de Default para no tener que 
     configurarlo manualmente
     
     El SecurityManager (sm) necesita un Realm y un Realm necesita el ini.
     En este caso pasaremos un nuevo realm al sm de forma directa y con el ini
     que estamos confugurando actualmente, pues no necesitamos un realm especial.
     **/
    private DefaultSecurityManager sm=new DefaultSecurityManager();
    private Ini ini = new Ini();
    
    //Esta es la sección de nuestro ini donde se almacenan los uduarios y contraseñas
    private Ini.Section usuarios=ini.addSection(IniRealm.USERS_SECTION_NAME);
    
    
    
    public Shiro() {
        inicializar();
    }
    
    private void inicializar(){
        // la contraseña es "adm"
        usuarios.put("adm","sJxgD93Fc/EXRJs3I/I9ZA==");  
        
        // la contraseña es "candidato"
        usuarios.put("candidato","kd7LJI+Ke+EiY7OYjSjN8A==");
        
        //Se setea el relm, con el ini inicializado
        sm.setRealm(new IniRealm(ini));
        
        //Se setea el securityManayer que estamos configurando
        SecurityUtils.setSecurityManager(sm);
    }
    
    /**
     * Aquí es donde actualizamos el sm y el SecurityUtils con el ini modificado
     **/
    private void actualizar (){
        sm.setRealm(new IniRealm(ini));
        SecurityUtils.setSecurityManager(sm);
    }
    
    /**Se agregan cuentas nuevas a nuestro ini y se mandan a actualizar los 
     * otros componentes con la ini modificada.
     **/
    public void agregarCuenta(String usuario, String clave){
        String claveEncriptada= encriptar(clave);
        usuarios.put(usuario, claveEncriptada);
        actualizar();
    }
    
    /**
     * Método encriptador
     **/
    public String encriptar(String aEncriptar){
        Hash hash = new Md5Hash(aEncriptar);
//        Si se necesita saber el resultado de la encriptación
//        System.out.println( hash.toBase64());        
        return hash.toBase64();
    }
   
    public void logIn (String usuario, String clave){
        //Un Subject es cualquier cosa que esté usando el sistema,
        //En este caso es una persona, pero es anonima y no esta identificada.
        Subject currentUser = SecurityUtils.getSubject();
        
        String claveEncriptada= encriptar(clave);
        UsernamePasswordToken token = new UsernamePasswordToken(usuario, claveEncriptada);
        try {
                currentUser.login(token);
            } catch (UnknownAccountException uae) {
                System.out.println("No hay usuario con el nombre " + token.getPrincipal() );
            } catch ( IncorrectCredentialsException ice ) {
                System.out.println("Password para la cuenta " + token.getPrincipal() + " es incorrecto");
            }
        
        if(currentUser.isAuthenticated()){
            System.out.println("Uusario [" + currentUser.getPrincipal().toString() + "] se ha logueado correctamente");
        }else{
            System.out.println("Error al iniciar sesión");
        }
    }
    
    /**
     * Por si se requiere obtener el usuario que está logueado
     * @return String
     **/
    public String getUsuario(){
        Subject currentUser = SecurityUtils.getSubject();
        return currentUser.getPrincipal().toString();
        
    }
    
    public void logOut (){
        Subject currentUser = SecurityUtils.getSubject();
        currentUser.logout();
        System.out.println("La sesión se ha cerrado correctamente");
    }
    
    
    
    public static void main(String[] args) {
        Shiro cs = new Shiro();
        
        Scanner u = new Scanner(System.in);
        Scanner c = new Scanner(System.in);
        
        System.out.println("Escriba el usuario a agregar");
        String usuario= u.nextLine();
        System.out.println("Escriba la contraseña");
        String clave= c.nextLine();
        
        cs.agregarCuenta(usuario, clave);
        cs.logIn(usuario, clave);
        cs.logOut();
        
        System.out.println("Demostrando que aún existen las otras cuentas:");
        cs.logIn("candidato", "candidato");
        System.out.println(cs.getUsuario());
        cs.logOut();
        System.out.println("La siguiente es una cuenta con una contraseña incorrecta:");
        cs.logIn("adm", "mad");
        cs.logOut();
        
    }

}
