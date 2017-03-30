package projetoaplicado.vgoncalves.com.projetoaplicado.Config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by vgoncalves on 29/03/2017.
 */

public class ConfiguracaoFirebase {
    private static DatabaseReference referenceFirebase;
    private static FirebaseAuth autenticador;

    public static DatabaseReference getReferenceFirebase(){
        if(referenceFirebase == null){
            referenceFirebase = FirebaseDatabase.getInstance().getReference();
        }
        return referenceFirebase;
    }
    public static FirebaseAuth getAutenticador(){
        if (autenticador == null){
            autenticador = FirebaseAuth.getInstance();
        }
        return autenticador;
    }
}
