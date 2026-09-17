package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandLogoRed
import com.example.ui.theme.BrandLogoTurquoise
import com.example.ui.theme.BrandLogoYellow

/**
 * Taim Ta Man (တိမ်တမန်) Brand Logo
 * Recreates the official circular brand mark with:
 * - Rich Crimson Red circular canvas (#D32042)
 * - Awning with alternating turquoise (#38B6FF) & yellow (#FFD54F) scallops
 * - Golden brand lettering "တိမ်တမန်"
 * - Authentic Rakhine & Seafood subtext
 * - Turquoise delivery food cart with spoked wheels
 */
@Composable
fun TaimTaManBrandLogo(
    modifier: Modifier = Modifier,
    size: Dp = 110.dp,
    showSubtext: Boolean = true
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(BrandLogoRed)
    ) {
        if (size >= 80.dp) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = (size.value * 0.08f).dp, horizontal = (size.value * 0.06f).dp)
            ) {
                // 1. Awning with turquoise & yellow stripes
                AwningStripesGraphic(
                    width = (size.value * 0.58f).dp,
                    height = (size.value * 0.18f).dp
                )

                // 2. Brand Name & Subtext
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 2.dp)
                ) {
                    Text(
                        text = "တိမ်တမန်",
                        fontWeight = FontWeight.Black,
                        fontSize = (size.value * 0.16f).sp,
                        color = BrandLogoYellow,
                        lineHeight = (size.value * 0.18f).sp,
                        textAlign = TextAlign.Center
                    )

                    if (showSubtext && size >= 100.dp) {
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = "ရခိုင်အစားအစာနှင့်ပင်လယ်စာ",
                            fontWeight = FontWeight.Medium,
                            fontSize = (size.value * 0.055f).sp,
                            color = BrandLogoYellow.copy(alpha = 0.95f),
                            lineHeight = (size.value * 0.065f).sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "အိမ်အရောက်ပို့ဆောင်ရေး",
                            fontWeight = FontWeight.Medium,
                            fontSize = (size.value * 0.050f).sp,
                            color = BrandLogoYellow.copy(alpha = 0.90f),
                            lineHeight = (size.value * 0.060f).sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // 3. Turquoise Food Cart at Bottom
                FoodCartGraphic(
                    width = (size.value * 0.54f).dp,
                    height = (size.value * 0.20f).dp
                )
            }
        } else {
            // Compact Mode (for top bars, avatars, or small badges)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            ) {
                AwningStripesGraphic(
                    width = (size.value * 0.65f).dp,
                    height = (size.value * 0.22f).dp
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = "တိမ်တမန်",
                    fontWeight = FontWeight.Bold,
                    fontSize = (size.value * 0.22f).sp,
                    color = BrandLogoYellow,
                    textAlign = TextAlign.Center
                )
                FoodCartGraphic(
                    width = (size.value * 0.60f).dp,
                    height = (size.value * 0.20f).dp
                )
            }
        }
    }
}

@Composable
private fun AwningStripesGraphic(
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(width = width, height = height)) {
        val stripeColors = listOf(
            BrandLogoTurquoise,
            BrandLogoYellow,
            BrandLogoTurquoise,
            BrandLogoYellow,
            BrandLogoTurquoise,
            BrandLogoYellow
        )
        val stripeCount = stripeColors.size
        val stripeWidth = size.width / stripeCount
        val mainHeight = size.height * 0.75f
        val scallopRadius = stripeWidth / 2f

        for (i in stripeColors.indices) {
            val startX = i * stripeWidth
            val color = stripeColors[i]

            // Main stripe body
            drawRect(
                color = color,
                topLeft = Offset(startX, 0f),
                size = Size(stripeWidth, mainHeight)
            )

            // Scalloped bottom tab
            val path = Path().apply {
                moveTo(startX, mainHeight)
                quadraticBezierTo(
                    startX + scallopRadius,
                    size.height,
                    startX + stripeWidth,
                    mainHeight
                )
                close()
            }
            drawPath(path = path, color = color)
        }
    }
}

@Composable
private fun FoodCartGraphic(
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(width = width, height = height)) {
        val cartColor = BrandLogoTurquoise
        val platformHeight = size.height * 0.22f
        val wheelRadius = size.height * 0.36f
        val wheelY = size.height - wheelRadius

        // Cart Platform
        drawRoundRect(
            color = cartColor,
            topLeft = Offset(0f, 0f),
            size = Size(size.width, platformHeight),
            cornerRadius = CornerRadius(4f, 4f)
        )

        // Left Wheel
        val leftWheelX = size.width * 0.24f
        drawCircle(
            color = cartColor,
            radius = wheelRadius,
            center = Offset(leftWheelX, wheelY),
            style = Stroke(width = 3f)
        )
        drawCircle(
            color = cartColor,
            radius = wheelRadius * 0.35f,
            center = Offset(leftWheelX, wheelY),
            style = Fill
        )

        // Right Wheel
        val rightWheelX = size.width * 0.76f
        drawCircle(
            color = cartColor,
            radius = wheelRadius,
            center = Offset(rightWheelX, wheelY),
            style = Stroke(width = 3f)
        )
        drawCircle(
            color = cartColor,
            radius = wheelRadius * 0.35f,
            center = Offset(rightWheelX, wheelY),
            style = Fill
        )
    }
}
