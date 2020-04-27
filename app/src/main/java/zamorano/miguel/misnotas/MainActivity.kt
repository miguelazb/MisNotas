package zamorano.miguel.misnotas

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nota_layout.view.*
import java.io.*

class MainActivity : AppCompatActivity() {

    var notas = ArrayList<Nota>()
    lateinit var adaptador: AdaptadorNotas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // notasDePrueba()

        fab.setOnClickListener{
            var intent = Intent(this, AgregarNotaActivity::class.java)
            startActivityForResult(intent, 123)
        }


        adaptador = AdaptadorNotas(this, notas)
        listview.adapter = adaptador


    }

    fun leerNotas(){
        notas.clear()
        var carpeta = File(ubicacion())

        if(carpeta.exists()){
            var archivos = carpeta.listFiles()
            if(archivos!=null){
                for(archivo in archivos){
                    leerArchivo(archivo)
                }
            }
        }

    }

    fun leerArchivo(archivo: File){
        val fis = FileInputStream(archivo)
        val dis = DataInputStream(fis)
        val br = BufferedReader(InputStreamReader(dis))
        var strLine: String? = br.readLine()
        var myData = ""

        while(strLine != null){
            myData = myData + strLine
            strLine = br.readLine()
        }

        br.close()
        dis.close()
        fis.close()

        var nombre = archivo.name.substring(0,archivo.name.length-4)
        var nota = Nota(nombre,myData)
        notas.add(nota)

    }

    fun ubicacion(): String {
        val carpeta = File(Environment.getExternalStorageDirectory(),"notas")
        if(!carpeta.exists()){
            carpeta.mkdir()
        }

        return carpeta.absolutePath
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 123) {
            leerNotas()
            adaptador.notifyDataSetChanged()
        }
    }

    fun notasDePrueba(){
        notas.add(Nota("Prueba 1", "Descripción nota 1"))
        notas.add(Nota("Prueba 2", "Descripción nota 2"))
        notas.add(Nota("Prueba 3", "Descripción nota 3"))
    }

}



class AdaptadorNotas: BaseAdapter {


    var context: Context
    var notas: ArrayList<Nota>

    constructor(context: Context, notas: ArrayList<Nota>){
        this.context = context
        this.notas = notas;
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var inflador = LayoutInflater.from(context);
        var vista = inflador.inflate(R.layout.nota_layout, null)
        var nota = notas[position]

        vista.tv_titulo.text = nota.titulo
        vista.tv_descripcion.text = nota.descripcion

        vista.btn_borrar.setOnClickListener{
            eliminar(nota.titulo)
            notas.remove(nota)
            this.notifyDataSetChanged()
        }

        return vista

    }

    fun eliminar(titulo: String){
        if(titulo == ""){
            Toast.makeText(context, "Error: título vacío", Toast.LENGTH_SHORT).show()

        } else {
            try {
                val archivo = File(ubicacion(), "${titulo}.txt")
                archivo.delete()

                Toast.makeText(context, "Se eliminó el archivo", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error al eliminar el archivo", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun ubicacion(): String {
        val carpeta = File(Environment.getExternalStorageDirectory(),"notas")
        if(!carpeta.exists()){
            carpeta.mkdir()
        }

        return carpeta.absolutePath
    }

    override fun getItem(position: Int): Any {
        return notas[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return notas.size
    }
}
