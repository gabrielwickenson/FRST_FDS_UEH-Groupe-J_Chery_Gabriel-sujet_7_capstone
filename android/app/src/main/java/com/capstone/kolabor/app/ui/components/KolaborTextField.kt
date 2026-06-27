package com.kolabor.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import com.capstone.serviceplatform.app.ui.theme.ErrorColor
import com.capstone.serviceplatform.app.ui.theme.Gray500
import com.capstone.serviceplatform.app.ui.theme.NavyLight
import com.capstone.serviceplatform.app.ui.theme.NavyPrimary

@Composable
fun KolaborTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isError: Boolean = false,
    errorMessage: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    enabled: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder, color = Gray500) },
        modifier = modifier.fillMaxWidth(),
        isError = isError,
        enabled = enabled,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = NavyPrimary,
            unfocusedIndicatorColor = NavyLight,
            errorIndicatorColor = ErrorColor,
            focusedLabelColor = NavyPrimary,
            errorLabelColor = ErrorColor,
        ),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        singleLine = true,
        supportingText = {
            if (isError && errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = ErrorColor
                )
            }
        }
    )
}