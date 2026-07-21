package com.capstone.kolabor.app.ui.auth

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.capstone.kolabor.app.data.model.RegisterRequest
import com.capstone.kolabor.app.data.repository.AuthRepository
import com.kolabor.app.ui.components.KolaborPrimaryButton
import com.kolabor.app.ui.components.KolaborTextField
import com.capstone.serviceplatform.app.ui.theme.*
import com.kolabor.app.R
import com.kolabor.app.ui.theme.space16
import com.kolabor.app.ui.theme.space24
import com.kolabor.app.ui.theme.space48
import com.kolabor.app.ui.theme.space8
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository(context) }

    var nom by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telephone by remember { mutableStateOf("") }
    var motDePasse by remember { mutableStateOf("") }
    var confirmMotDePasse by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var role by remember { mutableStateOf("CLIENT") }
    var acceptTerms by remember { mutableStateOf(false) }

    // Champs Prestataire
    var metier by remember { mutableStateOf("") }
    var zoneIntervention by remember { mutableStateOf("") }
    var anneesExperience by remember { mutableStateOf("") }
    var tarifHoraire by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val passwordsMatch = motDePasse == confirmMotDePasse && motDePasse.isNotBlank()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = space24)
                .padding(top = space48, bottom = space24)
        ) {
            // --- Header / Logo ---
            Image(
                painter = painterResource(id = R.drawable.logo_kolabor_svg),
                contentDescription = "Logo Kolabor",
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .height(64.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(space24))

            // ─── Titre ───
            Text(
                text = "Créer un compte",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = NavyPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Rejoignez des milliers d'utilisateurs Kolabor.",
                style = MaterialTheme.typography.bodyMedium,
                color = Gray500
            )

            Spacer(modifier = Modifier.height(space24))

            // ─── Segmented control animé : Client / Professionnel ───
            RoleSelector(
                selectedRole = role,
                onRoleSelected = { role = it }
            )

            Spacer(modifier = Modifier.height(space24))

            KolaborTextField(
                value = nom,
                onValueChange = { nom = it },
                label = "Nom complet",
                placeholder = "Jean Dupont"
            )

            Spacer(modifier = Modifier.height(space16))

            KolaborTextField(
                value = email,
                onValueChange = { email = it },
                label = "Adresse e-mail",
                placeholder = "vous@email.com",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(space16))

            FieldLabel("Téléphone", required = false)
            Spacer(modifier = Modifier.height(6.dp))
            PhoneField(
                value = telephone,
                onValueChange = { telephone = it }
            )

            Spacer(modifier = Modifier.height(space16))

            // ─── Bloc informations professionnelles (conditionnel + animé) ───
            AnimatedVisibility(
                visible = role == "PRESTATAIRE",
                enter = fadeIn(tween(250)) + expandVertically(tween(250)),
                exit = fadeOut(tween(150)) + shrinkVertically(tween(150))
            ) {
                Column {
                    ProfessionalInfoBox(
                        metier = metier,
                        onMetierChange = { metier = it },
                        zoneIntervention = zoneIntervention,
                        onZoneChange = { zoneIntervention = it },
                        anneesExperience = anneesExperience,
                        onAnneesChange = { anneesExperience = it },
                        tarifHoraire = tarifHoraire,
                        onTarifChange = { tarifHoraire = it }
                    )
                    Spacer(modifier = Modifier.height(space16))
                }
            }

            FieldLabel("Mot de passe", required = false)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = motDePasse,
                onValueChange = { motDePasse = it },
                placeholder = { Text("••••••••", color = Gray500) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = GreenPrimary,
                    unfocusedIndicatorColor = Gray300,
                    errorIndicatorColor = ErrorColor
                ),
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = icon, contentDescription = null, tint = Gray500)
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(space16))

            FieldLabel("Confirmer le mot de passe", required = false)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = confirmMotDePasse,
                onValueChange = { confirmMotDePasse = it },
                placeholder = { Text("••••••••", color = Gray500) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                isError = !passwordsMatch && confirmMotDePasse.isNotBlank(),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = GreenPrimary,
                    unfocusedIndicatorColor = Gray300,
                    errorIndicatorColor = ErrorColor
                ),
                trailingIcon = {
                    val icon = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(imageVector = icon, contentDescription = null, tint = Gray500)
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                supportingText = {
                    AnimatedVisibility(visible = !passwordsMatch && confirmMotDePasse.isNotBlank()) {
                        Text(
                            "Les mots de passe ne correspondent pas",
                            color = ErrorColor,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(space16))

            // ─── Checkbox conditions générales ───
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = acceptTerms,
                    onCheckedChange = { acceptTerms = it },
                    colors = CheckboxDefaults.colors(checkedColor = GreenPrimary)
                )
                Spacer(modifier = Modifier.width(4.dp))
                val annotatedText = buildAnnotatedString {
                    append("J'accepte les ")
                    withStyle(SpanStyle(color = GreenPrimary, fontWeight = FontWeight.SemiBold)) {
                        append("conditions générales")
                    }
                    append(" et la politique de confidentialité.")
                }
                Text(
                    text = annotatedText,
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray500,
                    modifier = Modifier.padding(top = 14.dp)
                )
            }

            AnimatedVisibility(
                visible = errorMessage != null,
                enter = fadeIn(tween(200)) + expandVertically(),
                exit = fadeOut(tween(150)) + shrinkVertically()
            ) {
                Text(
                    text = errorMessage.orEmpty(),
                    color = ErrorColor,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = space8)
                )
            }

            Spacer(modifier = Modifier.height(space24))

            // ─── Bouton principal vert ───
            Button(
                onClick = {
                    if (nom.isBlank() || email.isBlank() || motDePasse.isBlank() || confirmMotDePasse.isBlank()) {
                        errorMessage = "Veuillez remplir tous les champs obligatoires."
                        return@Button
                    }
                    if (!passwordsMatch) {
                        errorMessage = "Les mots de passe ne correspondent pas."
                        return@Button
                    }
                    if (!acceptTerms) {
                        errorMessage = "Veuillez accepter les conditions générales."
                        return@Button
                    }
                    if (role == "PRESTATAIRE" && (metier.isBlank() || tarifHoraire.isBlank() || zoneIntervention.isBlank())) {
                        errorMessage = "Veuillez remplir les informations professionnelles."
                        return@Button
                    }
                    isLoading = true
                    errorMessage = null

                    val request = RegisterRequest(
                        nom = nom,
                        email = email,
                        motDePasse = motDePasse,
                        role = role,
                        adresseParDefaut = null,
                        competences = if (role == "PRESTATAIRE") metier else null,
                        tarifHoraire = if (role == "PRESTATAIRE") tarifHoraire.toDoubleOrNull() else null,
                        zoneIntervention = if (role == "PRESTATAIRE") zoneIntervention else null
                    )

                    coroutineScope.launch {
                        try {
                            val success = authRepository.register(request)
                            if (success) {
                                onRegisterSuccess()
                            } else {
                                errorMessage = "Erreur lors de l'inscription. Email peut-être déjà utilisé."
                            }
                        } catch (e: Exception) {
                            Log.e("RegisterScreen", "Erreur", e)
                            errorMessage = "Erreur réseau. Vérifiez votre connexion."
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading && passwordsMatch,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenPrimary,
                    disabledContainerColor = GreenPrimary.copy(alpha = 0.5f)
                )
            ) {
                Text(
                    text = if (isLoading) "Création en cours..." else "Créer mon compte",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(space16))

            // ─── Lien connexion ───
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Déjà un compte ? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Gray500
                )
                Text(
                    text = "Se connecter",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = GreenPrimary,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }
    }
}

/**
 * Libellé de champ, gras, avec astérisque optionnel — utilisé pour les champs
 * qui ne passent pas par KolaborTextField (Téléphone, Mot de passe, Upload).
 */
@Composable
private fun FieldLabel(text: String, required: Boolean) {
    Row {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = Gray900
        )
        if (required) {
            Text(
                text = " *",
                style = MaterialTheme.typography.labelLarge,
                color = ErrorColor
            )
        }
    }
}

/**
 * Champ téléphone avec préfixe +509 non éditable.
 */
@Composable
private fun PhoneField(
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Gray300, RoundedCornerShape(12.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .background(Gray100)
                .padding(horizontal = space16),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+509",
                style = MaterialTheme.typography.bodyMedium,
                color = Gray500,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.width(space16))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = Gray900),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier.weight(1f),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = "55 66 7788",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Gray500
                    )
                }
                innerTextField()
            }
        )
    }
}

/**
 * Bloc encadré vert pour les informations professionnelles.
 */
@Composable
private fun ProfessionalInfoBox(
    metier: String,
    onMetierChange: (String) -> Unit,
    zoneIntervention: String,
    onZoneChange: (String) -> Unit,
    anneesExperience: String,
    onAnneesChange: (String) -> Unit,
    tarifHoraire: String,
    onTarifChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(GreenLightest)
            .border(1.dp, GreenPrimary.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            .padding(space16)
    ) {
        Text(
            text = "Informations professionnelles",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = NavyPrimary
        )
        Spacer(modifier = Modifier.height(space16))

        KolaborTextField(
            value = metier,
            onValueChange = onMetierChange,
            label = "Métier / Catégorie",
            placeholder = "Entretien de la maison"
        )

        Spacer(modifier = Modifier.height(space16))

        Row(horizontalArrangement = Arrangement.spacedBy(space16), modifier = Modifier.fillMaxWidth()) {
            KolaborTextField(
                value = zoneIntervention,
                onValueChange = onZoneChange,
                label = "Zone d'intervention",
                placeholder = "Port-au-Prince",
                modifier = Modifier.weight(1f)
            )
            KolaborTextField(
                value = anneesExperience,
                onValueChange = onAnneesChange,
                label = "Années d'expérience",
                placeholder = "8",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(space16))

        KolaborTextField(
            value = tarifHoraire,
            onValueChange = onTarifChange,
            label = "Tarif horaire (Gdes)",
            placeholder = "250",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(space16))

        FieldLabel("Pièce d'identité (vérification)", required = false)
        Spacer(modifier = Modifier.height(6.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .border(1.dp, GreenPrimary.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                .padding(vertical = space24),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.CloudUpload,
                contentDescription = null,
                tint = GreenPrimary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(space8))
            Text(
                text = "Déposer votre pièce",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = NavyPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "PNG, JPG, PDF jusqu'à 10 Mo",
                style = MaterialTheme.typography.bodySmall,
                color = Gray500
            )
        }
    }
}

/**
 * Segmented control avec pill glissante animée (spring) — le fond navy se déplace
 * physiquement d'une option à l'autre au lieu d'un simple changement de couleur brut.
 * C'est ce détail de mouvement continu qui donne le rendu "ultra senior".
 */
@Composable
private fun RoleSelector(
    selectedRole: String,
    onRoleSelected: (String) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Gray100)
    ) {
        val optionWidth = maxWidth / 2
        val indicatorOffset by animateDpAsState(
            targetValue = if (selectedRole == "CLIENT") 0.dp else optionWidth,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            ),
            label = "role_indicator_offset"
        )

        // Pill navy qui glisse physiquement
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(optionWidth)
                .fillMaxHeight()
                .padding(3.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(NavyPrimary)
        )

        Row(modifier = Modifier.fillMaxSize()) {
            RoleOption(
                label = "Je suis client",
                selected = selectedRole == "CLIENT",
                onClick = { onRoleSelected("CLIENT") },
                modifier = Modifier.weight(1f)
            )
            RoleOption(
                label = "Je suis professionnel",
                selected = selectedRole == "PRESTATAIRE",
                onClick = { onRoleSelected("PRESTATAIRE") },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun RoleOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (selected) Color.White else Gray500,
        animationSpec = tween(200),
        label = "role_text_color"
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}