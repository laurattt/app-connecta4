package com.lauratoro.app_connecta4

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val columnas = 7
    private val filas = 6
    private lateinit var tablero: MutableList<MutableList<ImageView>>
    private var turnoJugador = "rojo"
    private lateinit var textoTurno: TextView
    private lateinit var tableLayout: TableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textoTurno = findViewById(R.id.textoTurno)
        tableLayout = findViewById(R.id.tableLayout)
        val botonReinicio = findViewById<Button>(R.id.botonReinicio)

        botonReinicio.setOnClickListener {
            reiniciarJuego()
        }

        construirTablero()
    }

    private fun construirTablero() {
        val anchoPantalla = resources.displayMetrics.widthPixels
        val tamanyoCelda = (anchoPantalla * 0.85 / columnas).toInt()
        val paddingCelda = 4

        tablero = mutableListOf()

        val filaBotonesLayout = TableRow(this)

        for (columna in 0..columnas - 1) {
            val boton = Button(this).apply {
                text = "↓"
                layoutParams = TableRow.LayoutParams(
                    tamanyoCelda,
                    TableRow.LayoutParams.WRAP_CONTENT
                )
                setOnClickListener {
                    onColumnaClic(columna)
                }
            }
            filaBotonesLayout.addView(boton)
        }
        tableLayout.addView(filaBotonesLayout)

        for (fila in 0..filas - 1) {
            val filaImagenes = mutableListOf<ImageView>()
            val filaLayout = TableRow(this)

            for (columna in 0..columnas - 1) {
                val celda = ImageView(this).apply {
                    layoutParams = TableRow.LayoutParams(tamanyoCelda, tamanyoCelda).apply {
                        setMargins(paddingCelda, paddingCelda, paddingCelda, paddingCelda)
                    }
                    setImageResource(R.drawable.circulo_vacio)
                    tag = "vacio"
                    scaleType = ImageView.ScaleType.FIT_CENTER
                }
                filaImagenes.add(celda)
                filaLayout.addView(celda)
            }
            tableLayout.addView(filaLayout)
            tablero.add(filaImagenes)
        }
    }

    private fun onColumnaClic(columna: Int) {
        for (fila in filas - 1 downTo 0) {
            val celda = tablero[fila][columna]
            val tag = celda.tag.toString()

            if (tag == "vacio") {
                if (turnoJugador == "rojo") {
                    celda.setImageResource(R.drawable.circulo_rojo)
                    celda.tag = "rojo"

                    if (verificarVictoria(turnoJugador)) {
                        mostrarDialogoVictoria(turnoJugador)
                    } else {
                        turnoJugador = "azul"
                        textoTurno.text = "Turno: Azul"
                    }
                } else {
                    celda.setImageResource(R.drawable.circulo_azul)
                    celda.tag = "azul"

                    if (verificarVictoria(turnoJugador)) {
                        mostrarDialogoVictoria(turnoJugador)
                    } else {
                        turnoJugador = "rojo"
                        textoTurno.text = "Turno: Rojo"
                    }
                }
                break
            }
        }
    }


    private fun verificarVictoria(jugador: String): Boolean {

        // vert
        for (columna in 0..columnas - 1) {
            for (fila in 0..filas - 4) {
                if (tablero[fila][columna].tag == jugador &&
                    tablero[fila + 1][columna].tag == jugador &&
                    tablero[fila + 2][columna].tag == jugador &&
                    tablero[fila + 3][columna].tag == jugador) {
                    return true
                }
            }
        }

        // horix
        for (fila in 0..filas - 1) {
            for (columna in 0..columnas - 4) {
                if (tablero[fila][columna].tag == jugador &&
                    tablero[fila][columna + 1].tag == jugador &&
                    tablero[fila][columna + 2].tag == jugador &&
                    tablero[fila][columna + 3].tag == jugador) {
                    return true
                }
            }
        }

        // diagonal a la derecha
        for (fila in 0..filas - 4) {
            for (columna in 0..columnas - 4) {
                if (tablero[fila][columna].tag == jugador &&
                    tablero[fila + 1][columna + 1].tag == jugador &&
                    tablero[fila + 2][columna + 2].tag == jugador &&
                    tablero[fila + 3][columna + 3].tag == jugador) {
                    return true
                }
            }
        }

        // diagonal a la izquierda
        for (fila in 0..filas - 4) {
            for (columna in 3..columnas - 1) {
                if (tablero[fila][columna].tag == jugador &&
                    tablero[fila + 1][columna - 1].tag == jugador &&
                    tablero[fila + 2][columna - 2].tag == jugador &&
                    tablero[fila + 3][columna - 3].tag == jugador) {
                    return true
                }
            }
        }

        return false
    }

    private fun mostrarDialogoVictoria(jugador: String) {
        AlertDialog.Builder(this)
            .setTitle("Victoria :D")
            .setMessage("Gana el jugador $jugador")
            .setPositiveButton("Nueva partida") { _, _ ->
                reiniciarJuego()
            }
            .setCancelable(false)
            .show()
    }

    private fun reiniciarJuego() {
        turnoJugador = "rojo"
        textoTurno.text = "Turno: Rojo"

        for (fila in 0..filas - 1) {
            for (columna in 0..columnas - 1) {
                val celda = tablero[fila][columna]
                celda.setImageResource(R.drawable.circulo_vacio)
                celda.tag = "vacio"
            }
        }
    }
}
