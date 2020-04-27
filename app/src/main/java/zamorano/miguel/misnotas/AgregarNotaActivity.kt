package zamorano.miguel.misnotas

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_agregar_nota.*
import android.Manifest
import android.os.Environment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.FileOutputStream

class AgregarNotaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_nota)
        btn_guardar.setOnClickListener{
            guardarNota();
        }


    }

    fun guardarNota(){
        // si no tiene permisos
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),235)
            // guardar si se dan permisos
        } else {
            guardar()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when(requestCode){
            235 -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    guardar()
                } else {
                    Toast.makeText(this, "Error: permisos denegados", Toast.LENGTH_SHORT).show()
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun guardar(){
        var titulo = et_titulo.text.toString()
        var descripcion = et_descripcion.text.toString()

        if(titulo=="" || descripcion == ""){
            Toast.makeText(this,"Error: campos vacíos", Toast.LENGTH_SHORT)
        } else {
            try {
                val archivo = File(ubicacion(), "${titulo}.txt")
                val fos = FileOutputStream(archivo)
                fos.write(descripcion.toByteArray())
                fos.close()
                Toast.makeText(this, "Se guardó el archivo en la carpeta pública", Toast.LENGTH_SHORT).show()
            } catch(e: Exception){
                Toast.makeText(this, "Error: no se pudo guardar el archivo", Toast.LENGTH_SHORT).show()
            }
        }
        finish()
    }

    private fun ubicacion(): String {
        val carpeta = File(Environment.getExternalStorageDirectory(),"notas")
        if(!carpeta.exists()){
            carpeta.mkdir()
        }

        return carpeta.absolutePath
    }

}
