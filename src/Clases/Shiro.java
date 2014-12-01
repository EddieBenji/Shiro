package Clases;

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
     * - Un módulo para inicio de sesión - Un módulo para
     * encriptar/descrncriptar las contraseñas - Otro para cargar los usuario en
     * caché.
     *
     */
    /**
     * El SecurityUtils, que es el que maneja todo en Shiro, necesita un
     * SecurityManager, en este caso usaré el de Default para no tener que
     * configurarlo manualmente
     *
     * El SecurityManager (sm) necesita un Realm y un Realm necesita el ini. En
     * este caso pasaremos un nuevo realm al sm de forma directa y con el ini
     * que estamos confugurando actualmente, pues no necesitamos un realm
     * especial.
     *
     */
    
    private DefaultSecurityManager sm;
    private Ini ini;
    //Esta es la sección de nuestro ini donde se almacenan los usuarios y contraseñas
    private Ini.Section usuarios;
    private Ini.Section roles;
    
    //el usuario actual:
    private Subject currentUser;

    public Shiro() {
        sm = new DefaultSecurityManager();
        ini = new Ini();
        usuarios = ini.addSection(IniRealm.USERS_SECTION_NAME);
        roles = ini.addSection(IniRealm.ROLES_SECTION_NAME);
        inicializar();
    }

    private void inicializar() {
        //Se setea el relm, con el ini inicializado
        sm.setRealm(new IniRealm(ini));
        //Se setea el securityManayer que estamos configurando
        SecurityUtils.setSecurityManager(sm);
    }

    /**
     * Aquí es donde actualizamos el sm y el SecurityUtils con el ini modificado
     *
     */
    private void actualizar() {
        sm.setRealm(new IniRealm(ini));
        SecurityUtils.setSecurityManager(sm);
    }
    
    /**
     * Se agregan roles que se vayan a utilizar
     * 
     * @param rol
     * @param Permisos
     **/
    public void agregarRol(String rol, String Permisos){
        roles.put(rol, Permisos);
        actualizar();
    }

    /**
     * Se agregan cuentas nuevas a nuestro ini y se mandan a actualizar los
     * otros componentes con la ini modificada.
     *
     * @param usuario
     * @param clave
     * @param rol
     *
     */
    public void agregarCuenta(String usuario, String clave, String rol) {
        usuarios.put(usuario, clave + ", " + rol);
        actualizar();
    }

    /**
     * Método encriptador
     *
     * @param aEncriptar
     * @return
     *
     */
    public String encriptar(String aEncriptar) {
        Hash hash = new Md5Hash(aEncriptar);
//        Si se necesita saber el resultado de la encriptación
//        System.out.println( hash.toBase64());        
        return hash.toBase64();
    }


    public boolean logIn(String usuario, String clave) 
            throws UnknownAccountException,IncorrectCredentialsException{
        //Un Subject es cualquier cosa que esté usando el sistema,
        //En este caso es una persona, pero es anonima y no esta identificada.
        currentUser = SecurityUtils.getSubject();

        String claveEncriptada = encriptar(clave);
        UsernamePasswordToken token = new UsernamePasswordToken(usuario, claveEncriptada);
        token.setRememberMe(true);
        
        currentUser.login(token);
        
        return currentUser.isAuthenticated();
    }

    /**
     * Por si se requiere obtener el usuario que está logueado
     *
     * @return String
     *
     */
    public String getUsuario() {
         currentUser = SecurityUtils.getSubject();
        return currentUser.getPrincipal().toString();

    }
    
    /** 
     * Revisa si el usuario tiene el rol que se le especifica.
     * 
     * @param rol
     * 
     * @return boolean
     **/
    public boolean hasRol(String rol){ 
        currentUser = SecurityUtils.getSubject();
        return currentUser.hasRole(rol);        
    }
    
    public boolean hasPermisos(String permiso){
        currentUser = SecurityUtils.getSubject();
        return currentUser.isPermitted(permiso);
    }
    
    

    public void logOut() {
        currentUser = SecurityUtils.getSubject();
        currentUser.logout();
    }

}
