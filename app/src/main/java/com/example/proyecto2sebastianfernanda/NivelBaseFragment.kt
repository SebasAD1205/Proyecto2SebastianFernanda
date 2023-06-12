package com.example.proyecto2sebastianfernanda


import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore

@Suppress("DEPRECATION")
class NivelBaseFragment : Fragment() {
    private lateinit var tvNombre: TextView
    private lateinit var tvScore: TextView
    private lateinit var ivAuno: ImageView
    private lateinit var ivAdos: ImageView
    private lateinit var ivVidas: ImageView
    private lateinit var ivSigno: ImageView
    private lateinit var etRespuesta: EditText

    private lateinit var mp: MediaPlayer
    private lateinit var mpGreat: MediaPlayer
    private lateinit var mpBad: MediaPlayer

    private lateinit var btnComprobar: Button

    private var numeroAleatorioUno = -1
    private var numeroAleatorioDos = -1
    private lateinit var nombreJugador: String
    private var resultado = 0
    private var score = 0
    private var vidas = 3
    private var nivel = 1
    private var scoreLimite = 9

    private var tituloNivel="Sumas básicas"
    private var signo = 1
    private var mensajeNivel = "Nivel $nivel - $tituloNivel"
    private lateinit var stringScoreAux: String

    private var posicionAudio = 0
    private var toastMessage:Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val currentOrientation = resources.configuration.orientation
        val rootView = if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            inflater.inflate(R.layout.fragment_base_nivel_land, container, false)
        } else {
            inflater.inflate(R.layout.fragment_base_nivel, container, false)
        }

        tvNombre = rootView.findViewById(R.id.tv_nombre)
        tvScore = rootView.findViewById(R.id.tv_score)
        ivVidas = rootView.findViewById(R.id.iv_vidas)
        ivAuno = rootView.findViewById(R.id.iv_Auno)
        ivAdos = rootView.findViewById(R.id.iv_Ados)
        ivSigno = rootView.findViewById(R.id.iv_Signo)
        etRespuesta = rootView.findViewById(R.id.et_respuesta)
        btnComprobar = rootView.findViewById(R.id.btnComprobar)

        if(savedInstanceState == null) {
            cancelarToast()
            toastMessage = Toast.makeText(requireContext(), mensajeNivel, Toast.LENGTH_SHORT)
            toastMessage?.show()

            nombreJugador = arguments?.getString("jugador")!!

            val stringScore = arguments?.getString("score")
            score = stringScore?.toInt() ?: 0

            val stringVidas = arguments?.getString("vidas")
            vidas = stringVidas?.toInt() ?: 3

            numeroAleatorio()
        }
        else{
            nombreJugador = savedInstanceState.getString("nombre")!!
            score = savedInstanceState.getInt("score")
            vidas = savedInstanceState.getInt("vidas")
            nivel = savedInstanceState.getInt("nivel")
            numeroAleatorioDos = savedInstanceState.getInt("numeroAleatorioUno")
            numeroAleatorioDos = savedInstanceState.getInt("numeroAleatorioDos")
            resultado = savedInstanceState.getInt("resultado")
            ivAuno.setImageResource(getDrawableId(numeroAleatorioUno))
            ivAdos.setImageResource(getDrawableId(numeroAleatorioDos))
            when(signo){
                1 -> ivSigno.setImageResource(R.drawable.adicion)
                2 -> ivSigno.setImageResource(R.drawable.resta)
                3 -> ivSigno.setImageResource(R.drawable.multiplicacion)
                4 -> ivSigno.setImageResource(R.drawable.division)
            }
            posicionAudio = savedInstanceState.getInt("posicionAudio")
            savedInstanceState.clear()
        }

        val stringJugador = "Jugador: $nombreJugador"
        tvNombre.text = stringJugador

        stringScoreAux = "Score: $score"
        tvScore.text = stringScoreAux

        mpGreat = MediaPlayer.create(requireContext(), R.raw.wonderful)
        mpBad = MediaPlayer.create(requireContext(), R.raw.bad)

        updateVidasUI()

        btnComprobar.setOnClickListener { comparar() }

        return rootView
    }

    override fun onStart() {
        super.onStart()
        mp = MediaPlayer.create(requireContext(), R.raw.alphabet_song)
        mp.seekTo(posicionAudio)
        mp.isLooping = true
        mp.start()
    }
    override fun onPause() {
        super.onPause()
        posicionAudio = mp.currentPosition
        mp.stop()
        mp.release()
        mpGreat.release()
        mpBad.release()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("nombre", nombreJugador)
        outState.putInt("score", score)
        outState.putInt("vidas", vidas)
        outState.putInt("nivel", nivel)
        outState.putInt("numeroAleatorioUno", numeroAleatorioUno)
        outState.putInt("numeroAleatorioDos", numeroAleatorioDos)
        outState.putInt("resultado", resultado)
        outState.putInt("signo", signo)
        outState.putInt("posicionAudio", posicionAudio)
    }

    private fun numeroAleatorio() {
        if (score <= scoreLimite) {
            when(nivel){
                1-> nivel1()
                2-> nivel2()
                3-> nivel3()
                4-> nivel4()
                5-> nivel5()
                6-> nivel6()
                7-> nivel7()
                8,9-> nivel8()
            }
        } else {
            if(nivel<=8) {
                scoreLimite += 10
                nivel++
            }
            else {
                scoreLimite = Int.MAX_VALUE
            }
            actualizarMensajeMetodo()
            cancelarToast()
            toastMessage = Toast.makeText(requireContext(), mensajeNivel, Toast.LENGTH_SHORT)
            toastMessage?.show()
            numeroAleatorio()
        }
    }

    private fun actualizarMensajeMetodo() {
        when(nivel) {
            2 -> tituloNivel = "Sumas intermedias"
            3 -> tituloNivel = "Restas básicas"
            4 -> tituloNivel = "Sumas y restas"
            5 -> tituloNivel = "Multiplicación"
            6 -> tituloNivel = "División"
            7 -> tituloNivel = "Multiplicacion y division"
            8 -> tituloNivel = "Todas las operaciones"
            9 -> tituloNivel = "Infinito"
        }
        mensajeNivel = "Nivel $nivel - $tituloNivel"
    }

    private fun getDrawableId(numero: Int): Int {
        return when (numero) {
            0 -> R.drawable.cero
            1 -> R.drawable.uno
            2 -> R.drawable.dos
            3 -> R.drawable.tres
            4 -> R.drawable.cuatro
            5 -> R.drawable.cinco
            6 -> R.drawable.seis
            7 -> R.drawable.siete
            8 -> R.drawable.ocho
            9 -> R.drawable.nueve
            else -> throw IllegalArgumentException("Número inválido: $numero")
        }
    }

    private fun baseDeDatos() {
        val bd = FirebaseFirestore.getInstance()
        val data = hashMapOf<String, Any>(
            "nombre" to nombreJugador,
            "score" to score
        )
        val doc = bd.collection("Jugadores")
            .document("3pIGRRf1se02EB8y6AvJ")

        doc.get().addOnSuccessListener {
            if(it !=null){
                val record = (it.get("score") as Long).toInt()
                if(record < score){
                    doc.update(data)
                }
            }
        }
    }

    private fun comparar() {
        var manzanas = "manzanas"
        var quedan = "quedan"
        val respuesta = etRespuesta.text.toString()
        if (respuesta.isNotEmpty()) {
            val respuestaJugador = respuesta.toInt()

            if (respuestaJugador == resultado) {
                mpGreat.start()
                score ++
                stringScoreAux = "Score: $score"
                tvScore.text = stringScoreAux

                cancelarToast()
                toastMessage =Toast.makeText(requireContext(), "Correcto!", Toast.LENGTH_SHORT)
                toastMessage?.show()
            } else {
                mpBad.start()
                vidas--
                updateVidasUI()

                if (vidas == 0) {
                    cancelarToast()
                    toastMessage = Toast.makeText(requireContext(), "¡Perdiste todas tus vidas!", Toast.LENGTH_SHORT)
                    toastMessage?.show()
                    baseDeDatos()
                    val fragment = MainFragment()
                    val bundle = Bundle()
                    fragment.arguments = bundle

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit()
                }
                else {
                    if(vidas == 1) {
                        manzanas = "manzana"
                        quedan = "queda"
                    }

                    cancelarToast()
                    toastMessage = Toast.makeText(requireContext(), "Te equivocaste! Te $quedan $vidas $manzanas!", Toast.LENGTH_SHORT)
                    toastMessage?.show()
                }

            }
            etRespuesta.setText("")
            numeroAleatorio()
        } else {
            cancelarToast()
            toastMessage = Toast.makeText(requireContext(), "Debes dar una respuesta", Toast.LENGTH_SHORT)
            toastMessage?.show()
        }
    }

    private fun updateVidasUI() {
        when (vidas) {
            3 -> {
                ivVidas.setImageResource(R.drawable.tresvidas)
            }
            2 -> {
                ivVidas.setImageResource(R.drawable.dosvidas)
            }
            1 -> {
                ivVidas.setImageResource(R.drawable.unavida)
            }
        }
    }

    private fun nivel1(){
        numeroAleatorioUno = (Math.random() * 10).toInt()
        numeroAleatorioDos = (Math.random() * 10).toInt()

        resultado = numeroAleatorioUno + numeroAleatorioDos
        signo = 1
        if (resultado > 10) {
            nivel1()
        }
        else {
            ivAuno.setImageResource(getDrawableId(numeroAleatorioUno))
            ivAdos.setImageResource(getDrawableId(numeroAleatorioDos))
        }
    }

    private fun nivel2(){
        numeroAleatorioUno = (Math.random() * 10).toInt()
        numeroAleatorioDos = (Math.random() * 10).toInt()
        resultado = numeroAleatorioUno + numeroAleatorioDos
        signo = 1
        ivAuno.setImageResource(getDrawableId(numeroAleatorioUno))
        ivAdos.setImageResource(getDrawableId(numeroAleatorioDos))
        ivSigno.setImageResource(R.drawable.adicion)
    }

    private fun nivel3(){
        numeroAleatorioUno = (Math.random() * 10).toInt()
        numeroAleatorioDos = (Math.random() * 10).toInt()
        resultado = numeroAleatorioUno - numeroAleatorioDos
        signo = 2
        if (resultado < 0) {
           nivel3()
        }
        else {
            ivAuno.setImageResource(getDrawableId(numeroAleatorioUno))
            ivAdos.setImageResource(getDrawableId(numeroAleatorioDos))
            ivSigno.setImageResource(R.drawable.resta)
        }
    }

    private fun nivel4(){
        numeroAleatorioUno = (Math.random() * 10).toInt()
        numeroAleatorioDos = (Math.random() * 10).toInt()
        if (numeroAleatorioUno > 0 && numeroAleatorioDos <= 4) {
            resultado = numeroAleatorioUno + numeroAleatorioDos
            signo = 1
            ivSigno.setImageResource(R.drawable.adicion)
        } else {
            resultado = (numeroAleatorioUno - numeroAleatorioDos)
            signo = 2
            ivSigno.setImageResource(R.drawable.resta)
            if(resultado<0)
                nivel4()
        }
        ivAuno.setImageResource(getDrawableId(numeroAleatorioUno))
        ivAdos.setImageResource(getDrawableId(numeroAleatorioDos))
    }

    private fun nivel5(){
        numeroAleatorioUno = (Math.random() * 10).toInt()
        numeroAleatorioDos = (Math.random() * 10).toInt()
        resultado = numeroAleatorioUno * numeroAleatorioDos
        signo = 3
        ivAuno.setImageResource(getDrawableId(numeroAleatorioUno))
        ivAdos.setImageResource(getDrawableId(numeroAleatorioDos))
        ivSigno.setImageResource(R.drawable.multiplicacion)
    }

    private fun nivel6(){
        numeroAleatorioUno = (Math.random() * 10).toInt()
        numeroAleatorioDos = (Math.random() * 10).toInt()
        if(numeroAleatorioDos == 0)
            nivel6()
        else if(numeroAleatorioUno % numeroAleatorioDos != 0) {
            nivel6()
        }
        else{
            resultado = numeroAleatorioUno / numeroAleatorioDos
            signo = 4
            ivAuno.setImageResource(getDrawableId(numeroAleatorioUno))
            ivAdos.setImageResource(getDrawableId(numeroAleatorioDos))
            ivSigno.setImageResource(R.drawable.division)
        }
    }

    private fun nivel7(){
        numeroAleatorioUno = (Math.random() * 10).toInt()
        numeroAleatorioDos = (Math.random() * 10).toInt()
        if (numeroAleatorioUno > 0 && numeroAleatorioDos <= 4) {
            resultado = numeroAleatorioUno * numeroAleatorioDos
            signo = 3
            ivSigno.setImageResource(R.drawable.multiplicacion)
        } else if(numeroAleatorioDos == 0){
            nivel7()
        }
        else if(numeroAleatorioUno.toDouble() % numeroAleatorioDos.toDouble() != 0.0) {
            nivel7()
        }
        else{
            resultado = numeroAleatorioUno / numeroAleatorioDos
            signo = 4
            ivAuno.setImageResource(getDrawableId(numeroAleatorioUno))
            ivAdos.setImageResource(getDrawableId(numeroAleatorioDos))
            ivSigno.setImageResource(R.drawable.division)
        }
        ivAuno.setImageResource(getDrawableId(numeroAleatorioUno))
        ivAdos.setImageResource(getDrawableId(numeroAleatorioDos))
    }

    private fun nivel8(){
        when(((Math.random() * 4) + 1).toInt()){
            1->nivel2()
            2->nivel3()
            3->nivel5()
            4->nivel6()
        }
    }

    private fun cancelarToast(){
        if (toastMessage!= null) {
            toastMessage?.cancel()
        }
    }
}
