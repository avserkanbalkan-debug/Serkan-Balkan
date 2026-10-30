package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentRose
import com.example.ui.theme.CardBorderDark
import com.example.ui.theme.CardDark
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.PrimaryIndigoLight
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.LgsUiState
import com.example.viewmodel.LgsViewModel

@Composable
fun SettingsDialog(
    uiState: LgsUiState,
    viewModel: LgsViewModel,
    onDismiss: () -> Unit
) {
    var keyText by remember(uiState.apiKey) { mutableStateOf(uiState.apiKey) }
    var passwordVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardDark,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = null,
                    tint = PrimaryIndigoLight,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "LGS Master Ayarları",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Gemini API Key Input
                Column {
                    Text(
                        text = "Gemini API Anahtarı:",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = keyText,
                        onValueChange = {
                            keyText = it
                            viewModel.setApiKeyInput(it)
                        },
                        placeholder = {
                            Text("AIzaSy... ile başlayan anahtar", color = TextMuted, fontSize = 11.sp)
                        },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Görünürlük",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryIndigo,
                            unfocusedBorderColor = CardBorderDark,
                            focusedContainerColor = SurfaceDark,
                            unfocusedContainerColor = SurfaceDark,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("api_key_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Test API Key Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { viewModel.verifyApiKey() },
                            enabled = !uiState.isVerifyingKey,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("verify_api_key_button")
                        ) {
                            if (uiState.isVerifyingKey) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Doğrulanıyor...", fontSize = 11.sp)
                            } else {
                                Text("Anahtarı Doğrula", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (uiState.isApiKeyValid != null) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (uiState.isApiKeyValid == true) AccentEmerald.copy(alpha = 0.2f) else AccentRose.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = if (uiState.isApiKeyValid == true) "✓ Geçerli" else "✗ Geçersiz",
                                    color = if (uiState.isApiKeyValid == true) AccentEmerald else AccentRose,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    if (uiState.apiStatusMessage.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = uiState.apiStatusMessage,
                            color = if (uiState.isApiKeyValid == true) AccentEmerald else AccentRose,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    } else {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Anahtarınızı Google AI Studio → 'Get API key' bölümünden ücretsiz alabilirsiniz.",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                // AI Generation Mode Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Canlı Yapay Zeka Üretimi",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Açıksa Gemini API ile yeni soru üretir, kapalıysa hazır MEB soru bankasını kullanır.",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                    Switch(
                        checked = uiState.useAiGeneration,
                        onCheckedChange = { viewModel.toggleUseAiGeneration(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PrimaryIndigo,
                            checkedTrackColor = PrimaryIndigo.copy(alpha = 0.4f)
                        )
                    )
                }

                // Net Calculation Rule
                Column {
                    Text(
                        text = "Net Hesaplama Kuralı:",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        NetRuleChip(
                            label = "MEB (3Y = 1D)",
                            selected = uiState.netPenaltyRatio == 3.0f,
                            onClick = { viewModel.setNetPenaltyRatio(3.0f) }
                        )
                        NetRuleChip(
                            label = "4Y = 1D",
                            selected = uiState.netPenaltyRatio == 4.0f,
                            onClick = { viewModel.setNetPenaltyRatio(4.0f) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Tamam", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun NetRuleChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clickable { onClick() }
            .border(
                1.dp,
                if (selected) PrimaryIndigo else CardBorderDark,
                RoundedCornerShape(8.dp)
            ),
        shape = RoundedCornerShape(8.dp),
        color = if (selected) PrimaryIndigo.copy(alpha = 0.25f) else SurfaceDark
    ) {
        Text(
            text = label,
            color = if (selected) PrimaryIndigoLight else TextSecondary,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}
