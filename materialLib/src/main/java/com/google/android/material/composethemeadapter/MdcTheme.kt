/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:JvmName("MdcTheme")
package com.google.android.material.composethemeadapter

import android.content.Context
import android.content.res.Resources
import android.view.View
import androidx.compose.material.Colors
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.res.use
import java.lang.reflect.Method

/**
 * A [MaterialTheme] which reads the corresponding values from a Material Components for Android
 * theme in the given [context].
 *
 * By default the text colors from any associated `TextAppearance`s from the theme are *not* read.
 * This is because setting a fixed color in the resulting [TextStyle] breaks the usage of
 * [androidx.compose.material.ContentAlpha] through [androidx.compose.material.LocalContentAlpha].
 * You can customize this through the [setTextColors] parameter.
 *
 * For [Shapes], the configuration layout direction is taken into account when reading corner sizes
 * of `ShapeAppearance`s from the theme. For example, [Shapes.medium.topStart] will be read from
 * `cornerSizeTopLeft` for [View.LAYOUT_DIRECTION_LTR] and `cornerSizeTopRight` for
 * [View.LAYOUT_DIRECTION_RTL].
 *
 * @param context The context to read the theme from.
 * @param readColors whether the read the MDC color palette from the [context]'s theme.
 * If `false`, the current value of [MaterialTheme.colors] is preserved.
 * @param readTypography whether the read the MDC text appearances from [context]'s theme.
 * If `false`, the current value of [MaterialTheme.typography] is preserved.
 * @param readShapes whether the read the MDC shape appearances from the [context]'s theme.
 * If `false`, the current value of [MaterialTheme.shapes] is preserved.
 * @param setTextColors whether to read the colors from the `TextAppearance`s associated from the
 * theme. Defaults to `false`.
 * @param setDefaultFontFamily whether to read and prioritize the `fontFamily` attributes from
 * [context]'s theme, over any specified in the MDC text appearances. Defaults to `false`.
 */
@Composable
fun MdcTheme(
    context: Context = LocalContext.current,
    readColors: Boolean = true,
    readTypography: Boolean = true,
    readShapes: Boolean = true,
    setTextColors: Boolean = false,
    setDefaultFontFamily: Boolean = false,
    content: @Composable () -> Unit
) {
    // We try and use the theme key value if available, which should be a perfect key for caching
    // and avoid the expensive theme lookups in re-compositions.
    //
    // If the key is not available, we use the Theme itself as a rough approximation. Using the
    // Theme instance as the key is not perfect, but it should work for 90% of cases.
    // It falls down when the theme is manually mutated after a composition has happened
    // (via `applyStyle()`, `rebase()`, `setTo()`), but the majority of apps do not use those.
    val key = context.theme.key ?: context.theme

    val layoutDirection = LocalLayoutDirection.current

    val themeParams = remember(key) {
        createMdcTheme(
            context = context,
            layoutDirection = layoutDirection,
            readColors = readColors,
            readTypography = readTypography,
            readShapes = readShapes,
            setTextColors = setTextColors,
            setDefaultFontFamily = setDefaultFontFamily
        )
    }

    MaterialTheme(
        colors = themeParams.colors ?: MaterialTheme.colors,
        typography = themeParams.typography ?: MaterialTheme.typography,
        shapes = themeParams.shapes ?: MaterialTheme.shapes,
    ) {
        // We update the LocalContentColor to match our onBackground. This allows the default
        // content color to be more appropriate to the theme background
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colors.onBackground,
            content = content
        )
    }
}

/**
 * This class contains the individual components of a [MaterialTheme]: [Colors], [Typography]
 * and [Shapes].
 */
data class ThemeParameters(
    val colors: Colors?,
    val typography: Typography?,
    val shapes: Shapes?
)

/**
 * This function creates the components of a [androidx.compose.material.MaterialTheme], reading the
 * values from an Material Components for Android theme.
 *
 * By default the text colors from any associated `TextAppearance`s from the theme are *not* read.
 * This is because setting a fixed color in the resulting [TextStyle] breaks the usage of
 * [androidx.compose.material.ContentAlpha] through [androidx.compose.material.LocalContentAlpha].
 * You can customize this through the [setTextColors] parameter.
 *
 * For [Shapes], the [layoutDirection] is taken into account when reading corner sizes of
 * `ShapeAppearance`s from the theme. For example, [Shapes.medium.topStart] will be read from
 * `cornerSizeTopLeft` for [LayoutDirection.Ltr] and `cornerSizeTopRight` for [LayoutDirection.Rtl].
 *
 * The individual components of the returned [ThemeParameters] may be `null`, depending on the
 * matching 'read' parameter. For example, if you set [readColors] to `false`,
 * [ThemeParameters.colors] will be null.
 *
 * @param context The context to read the theme from.
 * @param layoutDirection The layout direction to be used when reading shapes.
 * @param density The current density.
 * @param readColors whether the read the MDC color palette from the [context]'s theme.
 * @param readTypography whether the read the MDC text appearances from [context]'s theme.
 * @param readShapes whether the read the MDC shape appearances from the [context]'s theme.
 * @param setTextColors whether to read the colors from the `TextAppearance`s associated from the
 * theme. Defaults to `false`.
 * @param setDefaultFontFamily whether to read and prioritize the `fontFamily` attributes from
 * [context]'s theme, over any specified in the MDC text appearances. Defaults to `false`.
 * @return [ThemeParameters] instance containing the resulting [Colors], [Typography]
 * and [Shapes].
 */
fun createMdcTheme(
    context: Context,
    layoutDirection: LayoutDirection,
    density: Density = Density(context),
    readColors: Boolean = true,
    readTypography: Boolean = true,
    readShapes: Boolean = true,
    setTextColors: Boolean = false,
    setDefaultFontFamily: Boolean = false
): ThemeParameters {
    return context.obtainStyledAttributes(R.styleable.ComposeThemeAdapterTheme).use { ta ->
        require(ta.hasValue(R.styleable.ComposeThemeAdapterTheme_isMaterialTheme)) {
            "createMdcTheme requires the host context's theme" +
                " to extend Theme.MaterialComponents"
        }

        val colors: Colors? = if (readColors) {
            /* First we'll read the Material color palette */
            val primary = ta.getComposeColor(R.styleable.ComposeThemeAdapterTheme_colorPrimary)
            val primaryVariant = ta.getComposeColor(R.styleable.ComposeThemeAdapterTheme_colorPrimaryVariant)
            val onPrimary = ta.getComposeColor(R.styleable.ComposeThemeAdapterTheme_colorOnPrimary)
            val secondary = ta.getComposeColor(R.styleable.ComposeThemeAdapterTheme_colorSecondary)
            val secondaryVariant = ta.getComposeColor(R.styleable.ComposeThemeAdapterTheme_colorSecondaryVariant)
            val onSecondary = ta.getComposeColor(R.styleable.ComposeThemeAdapterTheme_colorOnSecondary)
            val background = ta.getComposeColor(R.styleable.ComposeThemeAdapterTheme_android_colorBackground)
            val onBackground = ta.getComposeColor(R.styleable.ComposeThemeAdapterTheme_colorOnBackground)
            val surface = ta.getComposeColor(R.styleable.ComposeThemeAdapterTheme_colorSurface)
            val onSurface = ta.getComposeColor(R.styleable.ComposeThemeAdapterTheme_colorOnSurface)
            val error = ta.getComposeColor(R.styleable.ComposeThemeAdapterTheme_colorError)
            val onError = ta.getComposeColor(R.styleable.ComposeThemeAdapterTheme_colorOnError)

            val isLightTheme = ta.getBoolean(R.styleable.ComposeThemeAdapterTheme_isLightTheme, true)

            if (isLightTheme) {
                lightColors(
                    primary = primary,
                    primaryVariant = primaryVariant,
                    onPrimary = onPrimary,
                    secondary = secondary,
                    secondaryVariant = secondaryVariant,
                    onSecondary = onSecondary,
                    background = background,
                    onBackground = onBackground,
                    surface = surface,
                    onSurface = onSurface,
                    error = error,
                    onError = onError
                )
            } else {
                darkColors(
                    primary = primary,
                    primaryVariant = primaryVariant,
                    onPrimary = onPrimary,
                    secondary = secondary,
                    secondaryVariant = secondaryVariant,
                    onSecondary = onSecondary,
                    background = background,
                    onBackground = onBackground,
                    surface = surface,
                    onSurface = onSurface,
                    error = error,
                    onError = onError
                )
            }
        } else null

        /**
         * Next we'll create a typography instance, using the Material Theme text appearances
         * for TextStyles.
         *
         * We create a normal 'empty' instance first to start from the defaults, then merge in our
         * created text styles from the Android theme.
         */

        val typography = if (readTypography) {
            val defaultFontFamily = if (setDefaultFontFamily) {
                val defaultFontFamilyWithWeight: FontFamilyWithWeight? = ta.getFontFamilyOrNull(
                    R.styleable.ComposeThemeAdapterTheme_fontFamily
                ) ?: ta.getFontFamilyOrNull(R.styleable.ComposeThemeAdapterTheme_android_fontFamily)
                defaultFontFamilyWithWeight?.fontFamily
            } else {
                null
            }
            Typography(defaultFontFamily = defaultFontFamily ?: FontFamily.Default).merge(
                h1 = textStyleFromTextAppearance(
                    context,
                    density,
                    ta.getResourceIdOrThrow(R.styleable.ComposeThemeAdapterTheme_textAppearanceHeadline1),
                    setTextColors,
                    defaultFontFamily
                ),
                h2 = textStyleFromTextAppearance(
                    context,
                    density,
                    ta.getResourceIdOrThrow(R.styleable.ComposeThemeAdapterTheme_textAppearanceHeadline2),
                    setTextColors,
                    defaultFontFamily
                ),
                h3 = textStyleFromTextAppearance(
                    context,
                    density,
                    ta.getResourceIdOrThrow(R.styleable.ComposeThemeAdapterTheme_textAppearanceHeadline3),
                    setTextColors,
                    defaultFontFamily
                ),
                h4 = textStyleFromTextAppearance(
                    context,
                    density,
                    ta.getResourceIdOrThrow(R.styleable.ComposeThemeAdapterTheme_textAppearanceHeadline4),
                    setTextColors,
                    defaultFontFamily
                ),
                h5 = textStyleFromTextAppearance(
                    context,
                    density,
                    ta.getResourceIdOrThrow(R.styleable.ComposeThemeAdapterTheme_textAppearanceHeadline5),
                    setTextColors,
                    defaultFontFamily
                ),
                h6 = textStyleFromTextAppearance(
                    context,
                    density,
                    ta.getResourceIdOrThrow(R.styleable.ComposeThemeAdapterTheme_textAppearanceHeadline6),
                    setTextColors,
                    defaultFontFamily
                ),
                subtitle1 = textStyleFromTextAppearance(
                    context,
                    density,
                    ta.getResourceIdOrThrow(R.styleable.ComposeThemeAdapterTheme_textAppearanceSubtitle1),
                    setTextColors,
                    defaultFontFamily
                ),
                subtitle2 = textStyleFromTextAppearance(
                    context,
                    density,
                    ta.getResourceIdOrThrow(R.styleable.ComposeThemeAdapterTheme_textAppearanceSubtitle2),
                    setTextColors,
                    defaultFontFamily
                ),
                body1 = textStyleFromTextAppearance(
                    context,
                    density,
                    ta.getResourceIdOrThrow(R.styleable.ComposeThemeAdapterTheme_textAppearanceBody1),
                    setTextColors,
                    defaultFontFamily
                ),
                body2 = textStyleFromTextAppearance(
                    context,
                    density,
                    ta.getResourceIdOrThrow(R.styleable.ComposeThemeAdapterTheme_textAppearanceBody2),
                    setTextColors,
                    defaultFontFamily
                ),
                button = textStyleFromTextAppearance(
                    context,
                    density,
                    ta.getResourceIdOrThrow(R.styleable.ComposeThemeAdapterTheme_textAppearanceButton),
                    setTextColors,
                    defaultFontFamily
                ),
                caption = textStyleFromTextAppearance(
                    context,
                    density,
                    ta.getResourceIdOrThrow(R.styleable.ComposeThemeAdapterTheme_textAppearanceCaption),
                    setTextColors,
                    defaultFontFamily
                ),
                overline = textStyleFromTextAppearance(
                    context,
                    density,
                    ta.getResourceIdOrThrow(R.styleable.ComposeThemeAdapterTheme_textAppearanceOverline),
                    setTextColors,
                    defaultFontFamily
                )
            )
        } else null

        /**
         * Now read the shape appearances, taking into account the layout direction.
         */
        val shapes = if (readShapes) {
            Shapes(
                small = parseShapeAppearance(
                    context = context,
                    id = ta.getResourceIdOrThrow(R.styleable.ComposeThemeAdapterTheme_shapeAppearanceSmallComponent),
                    fallbackShape = emptyShapes.small,
                    layoutDirection = layoutDirection
                ),
                medium = parseShapeAppearance(
                    context = context,
                    id = ta.getResourceIdOrThrow(R.styleable.ComposeThemeAdapterTheme_shapeAppearanceMediumComponent),
                    fallbackShape = emptyShapes.medium,
                    layoutDirection = layoutDirection
                ),
                large = parseShapeAppearance(
                    context = context,
                    id = ta.getResourceIdOrThrow(R.styleable.ComposeThemeAdapterTheme_shapeAppearanceLargeComponent),
                    fallbackShape = emptyShapes.large,
                    layoutDirection = layoutDirection
                )
            )
        } else null

        ThemeParameters(colors, typography, shapes)
    }
}

private val emptyShapes = Shapes()

/**
 * This is gross, but we need a way to check for theme equality. Theme does not implement
 * `equals()` or `hashCode()`, but it does have a hidden method called `getKey()`.
 *
 * The cost of this reflective invoke is a lot cheaper than the full theme read which can
 * happen on each re-composition.
 */
private inline val Resources.Theme.key: Any?
    get() {
        if (!sThemeGetKeyMethodFetched) {
            try {
                @Suppress("PrivateApi")
                sThemeGetKeyMethod = Resources.Theme::class.java.getDeclaredMethod("getKey")
                    .apply { isAccessible = true }
            } catch (e: ReflectiveOperationException) {
                // Failed to retrieve Theme.getKey method
            }
            sThemeGetKeyMethodFetched = true
        }
        if (sThemeGetKeyMethod != null) {
            return try {
                sThemeGetKeyMethod?.invoke(this)
            } catch (e: ReflectiveOperationException) {
                // Failed to invoke Theme.getKey()
            }
        }
        return null
    }

private var sThemeGetKeyMethodFetched = false
private var sThemeGetKeyMethod: Method? = null
