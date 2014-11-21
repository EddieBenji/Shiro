package Clases;

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

}
