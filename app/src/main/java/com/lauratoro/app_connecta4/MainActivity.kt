package com.lauratoro.app_connecta4

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.util.Log

class MainActivity : AppCompatActivity() {
    private val columnes = 7
    private val files = 6
    private lateinit var taulell: MutableList<MutableList<View>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        construirTaulell()
    }

    private fun construirTaulell() {
        val baseLayout = findViewById<ConstraintLayout>(R.id.main)
        val textViewTitle = findViewById<TextView>(R.id.textViewTitle)

        // Creem TableLayout
        val tableLayout = TableLayout(this).apply {
            id = View.generateViewId()
            layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT, // L'amplada ocuparà tot l'espai disponible
                TableLayout.LayoutParams.WRAP_CONTENT // S'ocuparà l'alçada necessària per dibuixar la TableLayout
            )
            isShrinkAllColumns = true // Fa les columnes més estretes si no hi ha espai disponible
            isStretchAllColumns = true // Les columnes s'enxamplen per ocupar el total de l'amplada
        }

        baseLayout.addView(tableLayout)

        // Situem el taulell sota el textView del títol
        val constraintSet = ConstraintSet()
        constraintSet.clone(baseLayout) // Copia les restriccions actuals del ConstraintLayout
        constraintSet.connect(tableLayout.id, ConstraintSet.TOP, textViewTitle.id, ConstraintSet.BOTTOM, 32) // Connecta la vora superior del TableLayout amb la vora inferior del textView amb un marge de 32 píxels
        constraintSet.connect(tableLayout.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START) // Connecta la vora esquerra del TableLayout amb la vora esquerra del ConstraintLayout global que hi ha sobre la pantalla sencera
        constraintSet.connect(tableLayout.id, ConstraintSet.END, ConstraintSet.PARENT_ID,ConstraintSet.END) // Connecta la vora dreta del TableLayout amb la vora dreta del ConstraintLayout global
        constraintSet.applyTo(baseLayout)

        // Calcular tamany de cel·la
        val ampladaPantalla = resources.displayMetrics.widthPixels
        val tamanyCasella = ampladaPantalla / columnes

        // Inicialitzem el taulell
        taulell = mutableListOf()

        // Fila extra de botons
        val filaBotons = mutableListOf<View>()
        val filaBotonsLayout = TableRow(this)

        for (columna in 0 until columnes) {
            val boto = Button(this).apply {
                text = "↓"
                layoutParams = TableRow.LayoutParams(tamanyCasella, tamanyCasella)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                setOnClickListener {
                    onColumnClick(columna)
                }
            }
            filaBotons.add(boto)
            filaBotonsLayout.addView(boto)
        }
        tableLayout.addView(filaBotonsLayout)
        taulell.add(filaBotons)

        // La resta de files
        for (fila in 0 until files) {
            val filaImageViews = mutableListOf<View>()
            val filaLayout = TableRow(this)

            for (columna in 0 until columnes) {
                val casella = ImageView(this).apply {
                    layoutParams = TableRow.LayoutParams(tamanyCasella,tamanyCasella).apply {
                        gravity = Gravity.CENTER
                        //setMargins(4,4,4,4)
                    }
                    setImageResource(R.drawable.cercle_buit)
                    tag = "buit"
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }
                filaImageViews.add(casella)
                filaLayout.addView(casella)
            }
            tableLayout.addView(filaLayout)
            taulell.add(filaImageViews)
        }
    }

    private var tornJugador = "vermell"

    private fun onColumnClick(columna: Int) {
        for (fila in files downTo 1) {
            val casella = taulell[fila][columna] as ImageView
            val tag = casella.tag as? String ?: "buit"

            if (tag == "buit") {
                when (tornJugador) {
                    "vermell" -> {
                        casella.setImageResource(R.drawable.cercle_vermell)
                        casella.tag = "vermell"
                        if (win(tornJugador)){
                            AlertDialog.Builder(this)
                                .setTitle("Final de partida!")
                                .setMessage("Ha guanyat el jugador de l'equip $tornJugador")
                                .setPositiveButton("OK",null)
                                .show()
                        } else {
                            tornJugador = "blau"
                        }
                    }
                    "blau" -> {
                        casella.setImageResource(R.drawable.cercle_blau)
                        casella.tag = "blau"
                        if (win(tornJugador)){
                            AlertDialog.Builder(this)
                                .setTitle("Final de partida!")
                                .setMessage("Ha guanyat el jugador de l'equip $tornJugador")
                                .setPositiveButton("OK",null)
                                .show()
                        } else {
                            tornJugador = "vermell"
                        }

                    }
                }
                break
            }
        }
    }

    private fun win (tornJugador: String): Boolean{
        // Comprovem si hi ha 4 fitxes seguides en vertical
        for (columna in 0 until columnes) {
            var fitxesSeguides = 0
            for (fila in files downTo 1) { // Quan fem un bucle per les files cal tenir en compte que hem de mirar des de la fila d'index 6 (la última) fins la d'índex 1 (la primera després de la fila de botons)
                var casella = taulell[fila][columna]
                var tag = casella.tag
                if (tag == tornJugador) {
                    fitxesSeguides += 1
                    if (fitxesSeguides == 4) {
                        return true
                    }
                } else {
                    fitxesSeguides = 0
                }
            }
        }

        // Comprovem si hi ha 4 fitxes seguides en horitzontal
        for (fila in files downTo 1) {
            var fitxesSeguides = 0
            for (columna in 0 until columnes) {
                var casella = taulell[fila][columna]
                var tag = casella.tag
                if (tag == tornJugador) {
                    fitxesSeguides += 1
                    if (fitxesSeguides == 4) {
                        return true
                    }
                } else {
                    fitxesSeguides = 0
                }

            }
        }

        // Comprovem si hi ha 4 fitxes seguides en diagonal
        // Comprobamos diagonal descendente derecha (↘)
        for (fila in 1..files - 3) { // de 1 a 3 (les úniques files que poden començar diagonals sense que surtin del taulell són les tres primeres perquè si comencem a la 4ta fila i acabaria a la 7a (que NO EXISTEIX)
            for (columna in 0..columnes - 4) { // de 0 a 3 (les úniques columnes que poden començar diagonals descendents cap a la dreta són les quatre primeres)
                if (
                    taulell[fila][columna].tag == tornJugador &&
                    taulell[fila + 1][columna + 1].tag == tornJugador &&
                    taulell[fila + 2][columna + 2].tag == tornJugador &&
                    taulell[fila + 3][columna + 3].tag == tornJugador
                ) {
                    return true
                }
            }
        }

        // Comprobamos diagonal descendente izquierda (↙)
        for (fila in 1..files - 3) { // de 1 a 3
            for (columna in 3 until columnes) { // de 3 a 6 (les úniques columnes que poden començar diagonals descendents cap a l'esquerra són les quatre últimes)
                if (
                    taulell[fila][columna].tag == tornJugador &&
                    taulell[fila + 1][columna - 1].tag == tornJugador &&
                    taulell[fila + 2][columna - 2].tag == tornJugador &&
                    taulell[fila + 3][columna - 3].tag == tornJugador
                ) {
                    return true
                }
            }
        }

        return false
    }
}