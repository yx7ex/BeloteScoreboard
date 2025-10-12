package com.github.yx7ex.belotescoreboard

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var team1TotalScoreTextView: TextView
    private lateinit var team2TotalScoreTextView: TextView

    private lateinit var team1RoundScoreEditText: EditText
    private lateinit var team2RoundScoreEditText: EditText
    private lateinit var roundBidEditText: EditText

    private var team1Hint: CharSequence? = null
    private var team2Hint: CharSequence? = null
    private var bidHint: CharSequence? = null

    private lateinit var addRoundScoreButton: Button
    private lateinit var resetScoresButton: Button
    private lateinit var buttonEditLastRound: Button

    private lateinit var scoreHistoryRecyclerView: RecyclerView
    private lateinit var scoreHistoryAdapter: ScoreHistoryAdapter
    private val roundScoresList = mutableListOf<RoundScore>()

    private lateinit var instructionLabel: TextView
    private var activeEditText: EditText? = null

    private lateinit var buttonKeyK: Button
    private lateinit var buttonKeyPlus: Button
    private lateinit var buttonKeyMinus: Button
    private lateinit var buttonKeyTk: Button

    private var totalTeam1Score = 0
    private var totalTeam2Score = 0
    private var currentRoundNumber = 1

    private var isEditMode = false
    private var editingRoundOriginalTeam1Score: String = ""
    private var editingRoundOriginalTeam2Score: String = ""
    private var editingRoundIndex = -1

    private var defaultTeam1TextColor: Int = 0
    private var defaultTeam2TextColor: Int = 0

    private var isGameFinished = false // Flag to check if win condition was met

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        team1TotalScoreTextView = findViewById(R.id.team1_total_score_text_view)
        team2TotalScoreTextView = findViewById(R.id.team2_total_score_text_view)
        team1RoundScoreEditText = findViewById(R.id.edittext_team1_round_score)
        team2RoundScoreEditText = findViewById(R.id.edittext_team2_round_score)
        roundBidEditText = findViewById(R.id.edittext_round_bid)

        addRoundScoreButton = findViewById(R.id.button_add_round_score)
        resetScoresButton = findViewById(R.id.reset_scores_button)
        buttonEditLastRound = findViewById(R.id.button_edit_last_round)
        scoreHistoryRecyclerView = findViewById(R.id.recycler_view_score_history)
        instructionLabel = findViewById(R.id.label_enter_scores_instruction)

        team1Hint = team1RoundScoreEditText.hint
        team2Hint = team2RoundScoreEditText.hint
        bidHint = roundBidEditText.hint

        buttonKeyK = findViewById(R.id.button_key_K)
        buttonKeyPlus = findViewById(R.id.button_key_plus)
        buttonKeyMinus = findViewById(R.id.button_key_minus)
        buttonKeyTk = findViewById(R.id.button_key_tk)

        defaultTeam1TextColor = team1TotalScoreTextView.currentTextColor
        defaultTeam2TextColor = team2TotalScoreTextView.currentTextColor

        scoreHistoryAdapter = ScoreHistoryAdapter(roundScoresList)
        scoreHistoryRecyclerView.layoutManager = LinearLayoutManager(this)
        scoreHistoryRecyclerView.adapter = scoreHistoryAdapter

        val dividerItemDecoration = DividerItemDecoration(scoreHistoryRecyclerView.context, (scoreHistoryRecyclerView.layoutManager as LinearLayoutManager).orientation)
        ContextCompat.getDrawable(this, R.drawable.horizontal_divider)?.let { dividerItemDecoration.setDrawable(it) }
        scoreHistoryRecyclerView.addItemDecoration(dividerItemDecoration)

        val universalFocusListener = View.OnFocusChangeListener { view, hasFocus ->
            val editText = view as EditText
            hideSystemKeyboard(editText)
            if (hasFocus) {
                activeEditText = editText
                instructionLabel.visibility = View.GONE
                editText.hint = ""
                updateCustomKeyboardState()
            } else {
                if (!team1RoundScoreEditText.hasFocus() && !team2RoundScoreEditText.hasFocus() && !roundBidEditText.hasFocus()) {
                    instructionLabel.visibility = View.VISIBLE
                    activeEditText = null
                    updateCustomKeyboardState()
                }
                if (editText.text.isEmpty()) {
                    when (editText.id) {
                        R.id.edittext_team1_round_score -> editText.hint = team1Hint
                        R.id.edittext_team2_round_score -> editText.hint = team2Hint
                        R.id.edittext_round_bid -> editText.hint = bidHint
                    }
                }
            }
        }
        team1RoundScoreEditText.onFocusChangeListener = universalFocusListener
        team2RoundScoreEditText.onFocusChangeListener = universalFocusListener
        roundBidEditText.onFocusChangeListener = universalFocusListener

        val clickListenerToHideKeyboard = View.OnClickListener { hideSystemKeyboard(it) }
        team1RoundScoreEditText.setOnClickListener(clickListenerToHideKeyboard)
        team2RoundScoreEditText.setOnClickListener(clickListenerToHideKeyboard)
        roundBidEditText.setOnClickListener(clickListenerToHideKeyboard)
        
        team1RoundScoreEditText.showSoftInputOnFocus = false
        team2RoundScoreEditText.showSoftInputOnFocus = false
        roundBidEditText.showSoftInputOnFocus = false

        setupCustomKeyboard()

        buttonEditLastRound.setOnClickListener {
            if (roundScoresList.isNotEmpty() && !isEditMode) {
                isEditMode = true
                editingRoundIndex = roundScoresList.size - 1
                val roundToEdit = roundScoresList[editingRoundIndex]

                editingRoundOriginalTeam1Score = roundToEdit.team1Score
                editingRoundOriginalTeam2Score = roundToEdit.team2Score

                team1RoundScoreEditText.setText(roundToEdit.team1Score)
                team2RoundScoreEditText.setText(roundToEdit.team2Score)
                roundBidEditText.setText(roundToEdit.bid)

                addRoundScoreButton.text = "Update Round"
                buttonEditLastRound.isEnabled = false
                resetScoresButton.isEnabled = false

                team1RoundScoreEditText.requestFocus()
                team1RoundScoreEditText.setSelection(team1RoundScoreEditText.text.length)
            }
        }

        addRoundScoreButton.setOnClickListener {
            val team1RoundScoreString = team1RoundScoreEditText.text.toString().trim()
            val team2RoundScoreString = team2RoundScoreEditText.text.toString().trim()
            val roundBidString = roundBidEditText.text.toString().trim()

            if (team1RoundScoreString.isBlank() || team2RoundScoreString.isBlank()) {
                Toast.makeText(this, "Please enter scores for both teams", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val team1ScoreValue = parseScore(team1RoundScoreString)
            val team2ScoreValue = parseScore(team2RoundScoreString)

            if (!team1RoundScoreString.equals("TK", ignoreCase = true) && (team1RoundScoreString.toIntOrNull() == null || team1ScoreValue < 0)) {
                Toast.makeText(this, "Team 1 score must be a positive number or TK", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!team2RoundScoreString.equals("TK", ignoreCase = true) && (team2RoundScoreString.toIntOrNull() == null || team2ScoreValue < 0)) {
                Toast.makeText(this, "Team 2 score must be a positive number or TK", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                if (isEditMode) {
                    if (editingRoundIndex != -1) {
                        totalTeam1Score -= parseScore(editingRoundOriginalTeam1Score)
                        totalTeam2Score -= parseScore(editingRoundOriginalTeam2Score)

                        totalTeam1Score += team1ScoreValue
                        totalTeam2Score += team2ScoreValue

                        val editedRound = roundScoresList[editingRoundIndex].copy(
                            team1Score = team1RoundScoreString,
                            team2Score = team2RoundScoreString,
                            bid = roundBidString
                        )
                        roundScoresList[editingRoundIndex] = editedRound
                        scoreHistoryAdapter.notifyItemChanged(editingRoundIndex)

                        isEditMode = false
                        editingRoundIndex = -1
                        addRoundScoreButton.text = "Add Round Score"
                        resetScoresButton.isEnabled = true
                        buttonEditLastRound.isEnabled = roundScoresList.isNotEmpty()
                    }
                } else {
                    totalTeam1Score += team1ScoreValue
                    totalTeam2Score += team2ScoreValue

                    roundScoresList.add(RoundScore(currentRoundNumber, team1RoundScoreString, team2RoundScoreString, roundBidString))
                    currentRoundNumber++
                    scoreHistoryAdapter.notifyItemInserted(roundScoresList.size - 1)
                    scoreHistoryRecyclerView.scrollToPosition(roundScoresList.size - 1)
                    buttonEditLastRound.isEnabled = true
                }

                updateDisplay()
                clearInputFields()
                checkWinCondition()

            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Invalid data format", Toast.LENGTH_SHORT).show()
            }
        }

        resetScoresButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Confirm Reset")
                .setMessage("Are you sure you want to reset all scores?")
                .setPositiveButton("Yes") { _, _ -> resetGame() }
                .setNegativeButton("No", null)
                .show()
        }
        updateDisplay()
        clearInputFields()
    }

    private fun resetGame() {
        totalTeam1Score = 0
        totalTeam2Score = 0
        roundScoresList.clear()
        currentRoundNumber = 1
        isGameFinished = false // Reset the game over flag
        scoreHistoryAdapter.notifyDataSetChanged()
        updateDisplay()
        clearInputFields()
        if (!team1RoundScoreEditText.hasFocus() && !team2RoundScoreEditText.hasFocus() && !roundBidEditText.hasFocus()) {
            instructionLabel.visibility = View.VISIBLE
        } else {
            instructionLabel.visibility = View.GONE
        }
        buttonEditLastRound.isEnabled = false
        if (isEditMode) { // If reset happens during edit mode
            isEditMode = false
            addRoundScoreButton.text = "Add Round Score"
        }
    }

    private fun checkWinCondition() {
        if (isGameFinished) return // Don't show dialog again if game is already won

        val team1HasWon = totalTeam1Score >= 301
        val team2HasWon = totalTeam2Score >= 301

        if (team1HasWon || team2HasWon) {
            // If scores are tied (and over 301), do nothing and let the game continue.
            if (totalTeam1Score == totalTeam2Score) {
                return
            }

            val winner: String
            val message: String

            if (totalTeam1Score > totalTeam2Score) {
                winner = "Team 1"
                message = "$winner has won the game with a score of $totalTeam1Score! Do you want to start a new game?"
            } else { // This now correctly implies totalTeam2Score > totalTeam1Score
                winner = "Team 2"
                message = "$winner has won the game with a score of $totalTeam2Score! Do you want to start a new game?"
            }

            isGameFinished = true
            AlertDialog.Builder(this)
                .setTitle("Game Over!")
                .setMessage(message)
                .setPositiveButton("Reset") { _, _ -> resetGame() }
                .setNegativeButton("No", null)
                .setCancelable(false)
                .show()
        }
    }

    private fun parseScore(scoreString: String): Int {
        return if (scoreString.equals("TK", ignoreCase = true)) {
            0
        } else {
            scoreString.toIntOrNull() ?: 0
        }
    }

    private fun hideSystemKeyboard(view: View) {
        view.post { 
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun updateCustomKeyboardState() {
        val isTeam1ScoreFocused = activeEditText?.id == R.id.edittext_team1_round_score
        val isTeam2ScoreFocused = activeEditText?.id == R.id.edittext_team2_round_score
        val isBidFocused = activeEditText?.id == R.id.edittext_round_bid

        buttonKeyK.isEnabled = isBidFocused
        buttonKeyPlus.isEnabled = isBidFocused
        buttonKeyMinus.isEnabled = isBidFocused
        
        buttonKeyTk.isEnabled = (isTeam1ScoreFocused || isTeam2ScoreFocused) && activeEditText != null

        if (activeEditText == null) {
             buttonKeyK.isEnabled = false
             buttonKeyPlus.isEnabled = false
             buttonKeyMinus.isEnabled = false
             buttonKeyTk.isEnabled = false
        }
    }

    private fun setupCustomKeyboard() {
        val keyClickListener = View.OnClickListener { view ->
            activeEditText?.let { currentFocus ->
                val button = view as Button
                val keyText = button.text.toString()
                val currentTextString = currentFocus.text.toString()
                val isTeamField = currentFocus.id == R.id.edittext_team1_round_score || currentFocus.id == R.id.edittext_team2_round_score

                if (isTeamField && currentTextString.equals("TK", ignoreCase = true) && keyText.all { it.isDigit() }) {
                    currentFocus.setText(keyText)
                    currentFocus.setSelection(currentFocus.text.length)
                } else {
                    val allowedInputs = when(currentFocus.id) {
                        R.id.edittext_team1_round_score, R.id.edittext_team2_round_score -> "0123456789"
                        R.id.edittext_round_bid -> "0123456789K+-"
                        else -> ""
                    }
                    if (keyText in allowedInputs.map { it.toString() }) {
                        if (isTeamField && keyText == "0" && currentTextString == "0") {
                            // Do not append another zero if it's already "0"
                        } else if (isTeamField && keyText != "0" && currentTextString == "0") {
                            currentFocus.setText(keyText) // Replace "0" with the new digit
                            currentFocus.setSelection(currentFocus.text.length)
                        } else {
                            currentFocus.append(keyText)
                        }
                    }
                }
            }
        }

        findViewById<Button>(R.id.button_key_K).setOnClickListener(keyClickListener)
        findViewById<Button>(R.id.button_key_plus).setOnClickListener(keyClickListener)
        findViewById<Button>(R.id.button_key_minus).setOnClickListener(keyClickListener)
        
        findViewById<Button>(R.id.button_key_0).setOnClickListener(keyClickListener)
        findViewById<Button>(R.id.button_key_1).setOnClickListener(keyClickListener)
        findViewById<Button>(R.id.button_key_2).setOnClickListener(keyClickListener)
        findViewById<Button>(R.id.button_key_3).setOnClickListener(keyClickListener)
        findViewById<Button>(R.id.button_key_4).setOnClickListener(keyClickListener)
        findViewById<Button>(R.id.button_key_5).setOnClickListener(keyClickListener)
        findViewById<Button>(R.id.button_key_6).setOnClickListener(keyClickListener)
        findViewById<Button>(R.id.button_key_7).setOnClickListener(keyClickListener)
        findViewById<Button>(R.id.button_key_8).setOnClickListener(keyClickListener)
        findViewById<Button>(R.id.button_key_9).setOnClickListener(keyClickListener)

        buttonKeyTk.setOnClickListener {
            activeEditText?.let { currentFocus ->
                if (currentFocus.id == R.id.edittext_team1_round_score || currentFocus.id == R.id.edittext_team2_round_score) {
                    currentFocus.setText("TK")
                    currentFocus.setSelection(currentFocus.text.length)
                }
            }
        }

        findViewById<Button>(R.id.button_key_del).setOnClickListener {
            activeEditText?.let {
                val currentText = it.text
                if (currentText.isNotEmpty()) {
                    it.text.delete(currentText.length - 1, currentText.length)
                }
            }
        }
    }

    private fun updateDisplay() {
        team1TotalScoreTextView.text = totalTeam1Score.toString()
        if (totalTeam1Score >= 301) {
            team1TotalScoreTextView.setTextColor(Color.RED)
        } else {
            team1TotalScoreTextView.setTextColor(defaultTeam1TextColor)
        }

        team2TotalScoreTextView.text = totalTeam2Score.toString()
        if (totalTeam2Score >= 301) {
            team2TotalScoreTextView.setTextColor(Color.RED)
        } else {
            team2TotalScoreTextView.setTextColor(defaultTeam2TextColor)
        }
    }

    private fun clearInputFields() {
        team1RoundScoreEditText.text.clear()
        team2RoundScoreEditText.text.clear()
        roundBidEditText.text.clear()

        if (team1Hint != null) team1RoundScoreEditText.hint = team1Hint
        if (team2Hint != null) team2RoundScoreEditText.hint = team2Hint
        if (bidHint != null) roundBidEditText.hint = bidHint

        roundBidEditText.requestFocus()
        activeEditText = roundBidEditText
        updateCustomKeyboardState()
    }
}
