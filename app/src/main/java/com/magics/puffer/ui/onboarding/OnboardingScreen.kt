package com.magics.puffer.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.magics.puffer.ui.theme.*

/**
 * Onboarding screen — collects user info to build their personalized quit plan.
 * Presented in steps with smooth slide animations between pages.
 */
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var currentStep by remember { mutableIntStateOf(0) }

    LaunchedEffect(state.isDone) {
        if (state.isDone) onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(PufferDeepNavy, Color(0xFF0D1025)))
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))

            // Step indicator dots
            StepIndicator(currentStep = currentStep, totalSteps = 3)

            Spacer(Modifier.height(32.dp))

            // Animated step content
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    slideInHorizontally { it } + fadeIn() togetherWith
                    slideOutHorizontally { -it } + fadeOut()
                },
                label = "onboarding_step"
            ) { step ->
                when (step) {
                    0 -> StepWelcome(
                        name = state.name,
                        onNameChange = viewModel::onNameChange,
                        onNext = { currentStep = 1 }
                    )
                    1 -> StepSmokingHabits(
                        cigsPerDay = state.cigarettesPerDay,
                        onCigsChange = viewModel::onCigsPerDayChange,
                        onNext = { currentStep = 2 }
                    )
                    2 -> StepPlanSetup(
                        pricePerPack = state.pricePerPack,
                        cigsPerPack = state.cigarettesPerPack,
                        planDays = state.planDays,
                        currency = state.currency,
                        onPriceChange = viewModel::onPriceChange,
                        onCigsPerPackChange = viewModel::onCigsPerPackChange,
                        onPlanDaysChange = viewModel::onPlanDaysChange,
                        onCurrencyChange = viewModel::onCurrencyChange,
                        onStart = viewModel::saveAndStart,
                        isSaving = state.isSaving
                    )
                }
            }
        }
    }
}

// ── Step Indicator ─────────────────────────────────────────────────────────
@Composable
private fun StepIndicator(currentStep: Int, totalSteps: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(totalSteps) { index ->
            val isActive = index == currentStep
            val width by animateDpAsState(targetValue = if (isActive) 32.dp else 8.dp, label = "dot_width")
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(width)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isActive) PufferViolet else PufferBorderGray)
            )
        }
    }
}

// ── Step 0: Welcome ────────────────────────────────────────────────────────
@Composable
private fun StepWelcome(
    name: String,
    onNameChange: (String) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("💨", fontSize = 72.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Welcome to Puffer",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "A personal tracker built by someone who quit, for people who want to quit. No pressure — just one day at a time.",
            style = MaterialTheme.typography.bodyMedium.copy(color = PufferGray, textAlign = TextAlign.Center),
            textAlign = TextAlign.Center
        )

        // Disclaimer — inside app, plain motivational language
        Spacer(Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(PufferDarkCard)
                .border(1.dp, PufferBorderGray, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Text(
                text = "ℹ️  This app is a personal motivation and habit tracker. It is not a substitute for professional advice. Always consult a doctor or pharmacist for health concerns.",
                style = MaterialTheme.typography.bodySmall.copy(color = PufferGray),
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(32.dp))
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Your name (optional)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = PufferViolet,
                unfocusedBorderColor = PufferBorderGray,
                focusedLabelColor    = PufferViolet,
                unfocusedLabelColor  = PufferGray,
                cursorColor          = PufferViolet,
                focusedTextColor     = PufferWhite,
                unfocusedTextColor   = PufferWhite
            )
        )

        Spacer(Modifier.height(24.dp))
        PufferPrimaryButton(
            text = "Let's Start →",
            onClick = onNext,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ── Step 1: Habits ─────────────────────────────────────────────────────────
@Composable
private fun StepSmokingHabits(
    cigsPerDay: Int,
    onCigsChange: (Int) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🚬", fontSize = 56.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            "Current Habit",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "How many cigarettes do you smoke per day right now?",
            style = MaterialTheme.typography.bodyMedium.copy(color = PufferGray),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(40.dp))

        // Big number display
        Text(
            text = "$cigsPerDay",
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = PufferViolet
            )
        )
        Text("cigarettes / day", style = MaterialTheme.typography.bodyMedium.copy(color = PufferGray))

        Spacer(Modifier.height(24.dp))
        Slider(
            value = cigsPerDay.toFloat(),
            onValueChange = { onCigsChange(it.toInt()) },
            valueRange = 1f..60f,
            colors = SliderDefaults.colors(
                thumbColor       = PufferViolet,
                activeTrackColor = PufferViolet,
                inactiveTrackColor = PufferBorderGray
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("1", style = MaterialTheme.typography.labelMedium)
            Text("60", style = MaterialTheme.typography.labelMedium)
        }

        Spacer(Modifier.height(40.dp))
        PufferPrimaryButton("Continue →", onNext, Modifier.fillMaxWidth())
    }
}

// ── Step 2: Plan Setup ─────────────────────────────────────────────────────
@Composable
private fun StepPlanSetup(
    pricePerPack: Float,
    cigsPerPack: Int,
    planDays: Int,
    currency: String,
    onPriceChange: (Float) -> Unit,
    onCigsPerPackChange: (Int) -> Unit,
    onPlanDaysChange: (Int) -> Unit,
    onCurrencyChange: (String) -> Unit,
    onStart: () -> Unit,
    isSaving: Boolean
) {
    val planOptions = listOf(30 to "1 Month", 60 to "2 Months", 90 to "3 Months (Recommended)")
    var priceText by remember { mutableStateOf(pricePerPack.toInt().toString()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("💰", fontSize = 56.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            "Plan & Savings",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Set up your savings tracker so we can show how much money you're earning back.",
            style = MaterialTheme.typography.bodyMedium.copy(color = PufferGray),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        // Price per pack input
        OutlinedTextField(
            value = priceText,
            onValueChange = { v ->
                priceText = v
                v.toFloatOrNull()?.let { onPriceChange(it) }
            },
            label = { Text("Price per pack ($currency)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = pufferTextFieldColors()
        )

        Spacer(Modifier.height(12.dp))

        // Cigarettes per pack
        OutlinedTextField(
            value = cigsPerPack.toString(),
            onValueChange = { it.toIntOrNull()?.let { v -> onCigsPerPackChange(v) } },
            label = { Text("Cigarettes per pack") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = pufferTextFieldColors()
        )

        Spacer(Modifier.height(12.dp))

        // Currency picker
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("RSD", "EUR", "USD", "GBP").forEach { curr ->
                FilterChip(
                    selected = currency == curr,
                    onClick = { onCurrencyChange(curr) },
                    label = { Text(curr) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PufferViolet,
                        selectedLabelColor = Color.White,
                        containerColor = PufferDarkCard,
                        labelColor = PufferGray
                    )
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // Plan duration selector
        Text(
            "How long is your journey?",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        planOptions.forEach { (days, label) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (planDays == days) PufferViolet.copy(alpha = 0.15f) else Color.Transparent)
                    .border(
                        1.dp,
                        if (planDays == days) PufferViolet else PufferBorderGray,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { onPlanDaysChange(days) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = planDays == days,
                    onClick  = { onPlanDaysChange(days) },
                    colors   = RadioButtonDefaults.colors(selectedColor = PufferViolet)
                )
                Spacer(Modifier.width(8.dp))
                Text(label, style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(24.dp))

        PufferPrimaryButton(
            text = if (isSaving) "Setting up..." else "Build My Plan 🚀",
            onClick = onStart,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSaving
        )
        Spacer(Modifier.height(24.dp))
    }
}

// ── Shared button ──────────────────────────────────────────────────────────
@Composable
fun PufferPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PufferViolet,
            contentColor   = Color.White,
            disabledContainerColor = PufferViolet.copy(alpha = 0.5f)
        )
    ) {
        Text(text, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    }
}

@Composable
fun pufferTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = PufferViolet,
    unfocusedBorderColor = PufferBorderGray,
    focusedLabelColor    = PufferViolet,
    unfocusedLabelColor  = PufferGray,
    cursorColor          = PufferViolet,
    focusedTextColor     = PufferWhite,
    unfocusedTextColor   = PufferWhite
)
