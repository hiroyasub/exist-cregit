begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|regex
package|;
end_package

begin_comment
comment|/**  * Non-instantiable class containing constant data definitions used by the various Regular Expression translators  *   * Copied from Saxon-HE 9.2 package net.sf.saxon.regex without change.  */
end_comment

begin_class
specifier|public
class|class
name|RegexData
block|{
specifier|public
specifier|static
specifier|final
name|String
name|categories
init|=
literal|"LMNPZSC"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|subCategories
init|=
literal|"LuLlLtLmLoMnMcMeNdNlNoPcPdPsPePiPfPoZsZlZpSmScSkSoCcCfCoCn"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|char
name|EOS
init|=
literal|'\0'
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|blockNames
init|=
block|{
literal|"BasicLatin"
block|,
literal|"Latin-1Supplement"
block|,
literal|"LatinExtended-A"
block|,
literal|"LatinExtended-B"
block|,
literal|"IPAExtensions"
block|,
literal|"SpacingModifierLetters"
block|,
literal|"CombiningDiacriticalMarks"
block|,
literal|"Greek"
block|,
literal|"Cyrillic"
block|,
literal|"Armenian"
block|,
literal|"Hebrew"
block|,
literal|"Arabic"
block|,
literal|"Syriac"
block|,
literal|"Thaana"
block|,
literal|"Devanagari"
block|,
literal|"Bengali"
block|,
literal|"Gurmukhi"
block|,
literal|"Gujarati"
block|,
literal|"Oriya"
block|,
literal|"Tamil"
block|,
literal|"Telugu"
block|,
literal|"Kannada"
block|,
literal|"Malayalam"
block|,
literal|"Sinhala"
block|,
literal|"Thai"
block|,
literal|"Lao"
block|,
literal|"Tibetan"
block|,
literal|"Myanmar"
block|,
literal|"Georgian"
block|,
literal|"HangulJamo"
block|,
literal|"Ethiopic"
block|,
literal|"Cherokee"
block|,
literal|"UnifiedCanadianAboriginalSyllabics"
block|,
literal|"Ogham"
block|,
literal|"Runic"
block|,
literal|"Khmer"
block|,
literal|"Mongolian"
block|,
literal|"LatinExtendedAdditional"
block|,
literal|"GreekExtended"
block|,
literal|"GeneralPunctuation"
block|,
literal|"SuperscriptsandSubscripts"
block|,
literal|"CurrencySymbols"
block|,
literal|"CombiningMarksforSymbols"
block|,
literal|"LetterlikeSymbols"
block|,
literal|"NumberForms"
block|,
literal|"Arrows"
block|,
literal|"MathematicalOperators"
block|,
literal|"MiscellaneousTechnical"
block|,
literal|"ControlPictures"
block|,
literal|"OpticalCharacterRecognition"
block|,
literal|"EnclosedAlphanumerics"
block|,
literal|"BoxDrawing"
block|,
literal|"BlockElements"
block|,
literal|"GeometricShapes"
block|,
literal|"MiscellaneousSymbols"
block|,
literal|"Dingbats"
block|,
literal|"BraillePatterns"
block|,
literal|"CJKRadicalsSupplement"
block|,
literal|"KangxiRadicals"
block|,
literal|"IdeographicDescriptionCharacters"
block|,
literal|"CJKSymbolsandPunctuation"
block|,
literal|"Hiragana"
block|,
literal|"Katakana"
block|,
literal|"Bopomofo"
block|,
literal|"HangulCompatibilityJamo"
block|,
literal|"Kanbun"
block|,
literal|"BopomofoExtended"
block|,
literal|"EnclosedCJKLettersandMonths"
block|,
literal|"CJKCompatibility"
block|,
literal|"CJKUnifiedIdeographsExtensionA"
block|,
literal|"CJKUnifiedIdeographs"
block|,
literal|"YiSyllables"
block|,
literal|"YiRadicals"
block|,
literal|"HangulSyllables"
block|,
comment|// surrogates excluded because there are never any *characters* with codes in surrogate range
comment|// "PrivateUse", excluded because 3.1 adds non-BMP ranges
literal|"CJKCompatibilityIdeographs"
block|,
literal|"AlphabeticPresentationForms"
block|,
literal|"ArabicPresentationForms-A"
block|,
literal|"CombiningHalfMarks"
block|,
literal|"CJKCompatibilityForms"
block|,
literal|"SmallFormVariants"
block|,
literal|"ArabicPresentationForms-B"
block|,
literal|"Specials"
block|,
literal|"HalfwidthandFullwidthForms"
block|,
literal|"Specials"
block|}
decl_stmt|;
comment|/**      * Names of blocks including ranges outside the BMP.      */
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|specialBlockNames
init|=
block|{
literal|"OldItalic"
block|,
comment|// TODO: these have disappeared from Schema 1.0 2nd edition, but are largely back in 1.1
literal|"Gothic"
block|,
literal|"Deseret"
block|,
literal|"ByzantineMusicalSymbols"
block|,
literal|"MusicalSymbols"
block|,
literal|"MathematicalAlphanumericSymbols"
block|,
literal|"CJKUnifiedIdeographsExtensionB"
block|,
literal|"CJKCompatibilityIdeographsSupplement"
block|,
literal|"Tags"
block|,
literal|"PrivateUse"
block|,
literal|"HighSurrogates"
block|,
literal|"HighPrivateUseSurrogates"
block|,
literal|"LowSurrogates"
block|,     }
decl_stmt|;
comment|// This file was automatically generated by CategoriesGen
specifier|public
specifier|static
specifier|final
name|String
name|CATEGORY_NAMES
init|=
literal|"NoLoMnCfLlNlPoLuMcNdSoSmCo"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
index|[]
index|[]
name|CATEGORY_RANGES
init|=
block|{
block|{
comment|// No
literal|0x10107
block|,
literal|0x10133
block|,
literal|0x10320
block|,
literal|0x10323
block|}
block|,
block|{
comment|// Lo
literal|0x10000
block|,
literal|0x1000b
block|,
literal|0x1000d
block|,
literal|0x10026
block|,
literal|0x10028
block|,
literal|0x1003a
block|,
literal|0x1003c
block|,
literal|0x1003d
block|,
literal|0x1003f
block|,
literal|0x1004d
block|,
literal|0x10050
block|,
literal|0x1005d
block|,
literal|0x10080
block|,
literal|0x100fa
block|,
literal|0x10300
block|,
literal|0x1031e
block|,
literal|0x10330
block|,
literal|0x10349
block|,
literal|0x10380
block|,
literal|0x1039d
block|,
literal|0x10450
block|,
literal|0x1049d
block|,
literal|0x10800
block|,
literal|0x10805
block|,
literal|0x10808
block|,
literal|0x10808
block|,
literal|0x1080a
block|,
literal|0x10835
block|,
literal|0x10837
block|,
literal|0x10838
block|,
literal|0x1083c
block|,
literal|0x1083c
block|,
literal|0x1083f
block|,
literal|0x1083f
block|,
literal|0x20000
block|,
literal|0x2a6d6
block|,
literal|0x2f800
block|,
literal|0x2fa1d
block|}
block|,
block|{
comment|// Mn
literal|0x1d167
block|,
literal|0x1d169
block|,
literal|0x1d17b
block|,
literal|0x1d182
block|,
literal|0x1d185
block|,
literal|0x1d18b
block|,
literal|0x1d1aa
block|,
literal|0x1d1ad
block|,
literal|0xe0100
block|,
literal|0xe01ef
block|}
block|,
block|{
comment|// Cf
literal|0x1d173
block|,
literal|0x1d17a
block|,
literal|0xe0001
block|,
literal|0xe0001
block|,
literal|0xe0020
block|,
literal|0xe007f
block|}
block|,
block|{
comment|// Ll
literal|0x10428
block|,
literal|0x1044f
block|,
literal|0x1d41a
block|,
literal|0x1d433
block|,
literal|0x1d44e
block|,
literal|0x1d454
block|,
literal|0x1d456
block|,
literal|0x1d467
block|,
literal|0x1d482
block|,
literal|0x1d49b
block|,
literal|0x1d4b6
block|,
literal|0x1d4b9
block|,
literal|0x1d4bb
block|,
literal|0x1d4bb
block|,
literal|0x1d4bd
block|,
literal|0x1d4c3
block|,
literal|0x1d4c5
block|,
literal|0x1d4cf
block|,
literal|0x1d4ea
block|,
literal|0x1d503
block|,
literal|0x1d51e
block|,
literal|0x1d537
block|,
literal|0x1d552
block|,
literal|0x1d56b
block|,
literal|0x1d586
block|,
literal|0x1d59f
block|,
literal|0x1d5ba
block|,
literal|0x1d5d3
block|,
literal|0x1d5ee
block|,
literal|0x1d607
block|,
literal|0x1d622
block|,
literal|0x1d63b
block|,
literal|0x1d656
block|,
literal|0x1d66f
block|,
literal|0x1d68a
block|,
literal|0x1d6a3
block|,
literal|0x1d6c2
block|,
literal|0x1d6da
block|,
literal|0x1d6dc
block|,
literal|0x1d6e1
block|,
literal|0x1d6fc
block|,
literal|0x1d714
block|,
literal|0x1d716
block|,
literal|0x1d71b
block|,
literal|0x1d736
block|,
literal|0x1d74e
block|,
literal|0x1d750
block|,
literal|0x1d755
block|,
literal|0x1d770
block|,
literal|0x1d788
block|,
literal|0x1d78a
block|,
literal|0x1d78f
block|,
literal|0x1d7aa
block|,
literal|0x1d7c2
block|,
literal|0x1d7c4
block|,
literal|0x1d7c9
block|}
block|,
block|{
comment|// Nl
literal|0x1034a
block|,
literal|0x1034a
block|}
block|,
block|{
comment|// Po
literal|0x10100
block|,
literal|0x10101
block|,
literal|0x1039f
block|,
literal|0x1039f
block|}
block|,
block|{
comment|// Lu
literal|0x10400
block|,
literal|0x10427
block|,
literal|0x1d400
block|,
literal|0x1d419
block|,
literal|0x1d434
block|,
literal|0x1d44d
block|,
literal|0x1d468
block|,
literal|0x1d481
block|,
literal|0x1d49c
block|,
literal|0x1d49c
block|,
literal|0x1d49e
block|,
literal|0x1d49f
block|,
literal|0x1d4a2
block|,
literal|0x1d4a2
block|,
literal|0x1d4a5
block|,
literal|0x1d4a6
block|,
literal|0x1d4a9
block|,
literal|0x1d4ac
block|,
literal|0x1d4ae
block|,
literal|0x1d4b5
block|,
literal|0x1d4d0
block|,
literal|0x1d4e9
block|,
literal|0x1d504
block|,
literal|0x1d505
block|,
literal|0x1d507
block|,
literal|0x1d50a
block|,
literal|0x1d50d
block|,
literal|0x1d514
block|,
literal|0x1d516
block|,
literal|0x1d51c
block|,
literal|0x1d538
block|,
literal|0x1d539
block|,
literal|0x1d53b
block|,
literal|0x1d53e
block|,
literal|0x1d540
block|,
literal|0x1d544
block|,
literal|0x1d546
block|,
literal|0x1d546
block|,
literal|0x1d54a
block|,
literal|0x1d550
block|,
literal|0x1d56c
block|,
literal|0x1d585
block|,
literal|0x1d5a0
block|,
literal|0x1d5b9
block|,
literal|0x1d5d4
block|,
literal|0x1d5ed
block|,
literal|0x1d608
block|,
literal|0x1d621
block|,
literal|0x1d63c
block|,
literal|0x1d655
block|,
literal|0x1d670
block|,
literal|0x1d689
block|,
literal|0x1d6a8
block|,
literal|0x1d6c0
block|,
literal|0x1d6e2
block|,
literal|0x1d6fa
block|,
literal|0x1d71c
block|,
literal|0x1d734
block|,
literal|0x1d756
block|,
literal|0x1d76e
block|,
literal|0x1d790
block|,
literal|0x1d7a8
block|}
block|,
block|{
comment|// Mc
literal|0x1d165
block|,
literal|0x1d166
block|,
literal|0x1d16d
block|,
literal|0x1d172
block|}
block|,
block|{
comment|// Nd
literal|0x104a0
block|,
literal|0x104a9
block|,
literal|0x1d7ce
block|,
literal|0x1d7ff
block|}
block|,
block|{
comment|// So
literal|0x10102
block|,
literal|0x10102
block|,
literal|0x10137
block|,
literal|0x1013f
block|,
literal|0x1d000
block|,
literal|0x1d0f5
block|,
literal|0x1d100
block|,
literal|0x1d126
block|,
literal|0x1d12a
block|,
literal|0x1d164
block|,
literal|0x1d16a
block|,
literal|0x1d16c
block|,
literal|0x1d183
block|,
literal|0x1d184
block|,
literal|0x1d18c
block|,
literal|0x1d1a9
block|,
literal|0x1d1ae
block|,
literal|0x1d1dd
block|,
literal|0x1d300
block|,
literal|0x1d356
block|}
block|,
block|{
comment|// Sm
literal|0x1d6c1
block|,
literal|0x1d6c1
block|,
literal|0x1d6db
block|,
literal|0x1d6db
block|,
literal|0x1d6fb
block|,
literal|0x1d6fb
block|,
literal|0x1d715
block|,
literal|0x1d715
block|,
literal|0x1d735
block|,
literal|0x1d735
block|,
literal|0x1d74f
block|,
literal|0x1d74f
block|,
literal|0x1d76f
block|,
literal|0x1d76f
block|,
literal|0x1d789
block|,
literal|0x1d789
block|,
literal|0x1d7a9
block|,
literal|0x1d7a9
block|,
literal|0x1d7c3
block|,
literal|0x1d7c3
block|}
block|,
block|{
comment|// Co
literal|0xf0000
block|,
literal|0xffffd
block|,
literal|0x100000
block|,
literal|0x10fffd
block|}
block|}
decl_stmt|;
comment|// end of generated code
comment|// This file was automatically generated by NamingExceptionsGen
comment|// class NamingExceptions {
comment|//    public static final String NMSTRT_INCLUDES =
comment|//            "\u003A\u005F\u02BB\u02BC\u02BD\u02BE\u02BF\u02C0\u02C1\u0559" +
comment|//            "\u06E5\u06E6\u212E";
comment|//    public static final String NMSTRT_EXCLUDE_RANGES =
comment|//            "\u00AA\u00BA\u0132\u0133\u013F\u0140\u0149\u0149\u017F\u017F" +
comment|//            "\u01C4\u01CC\u01F1\u01F3\u01F6\u01F9\u0218\u0233\u02A9\u02AD" +
comment|//            "\u03D7\u03D7\u03DB\u03DB\u03DD\u03DD\u03DF\u03DF\u03E1\u03E1" +
comment|//            "\u0400\u0400\u040D\u040D\u0450\u0450\u045D\u045D\u048C\u048F" +
comment|//            "\u04EC\u04ED\u0587\u0587\u06B8\u06B9\u06BF\u06BF\u06CF\u06CF" +
comment|//            "\u06FA\u07A5\u0950\u0950\u0AD0\u0AD0\u0D85\u0DC6\u0E2F\u0E2F" +
comment|//            "\u0EAF\u0EAF\u0EDC\u0F00\u0F6A\u1055\u1101\u1101\u1104\u1104" +
comment|//            "\u1108\u1108\u110A\u110A\u110D\u110D\u1113\u113B\u113D\u113D" +
comment|//            "\u113F\u113F\u1141\u114B\u114D\u114D\u114F\u114F\u1151\u1153" +
comment|//            "\u1156\u1158\u1162\u1162\u1164\u1164\u1166\u1166\u1168\u1168" +
comment|//            "\u116A\u116C\u116F\u1171\u1174\u1174\u1176\u119D\u119F\u11A2" +
comment|//            "\u11A9\u11AA\u11AC\u11AD\u11B0\u11B6\u11B9\u11B9\u11BB\u11BB" +
comment|//            "\u11C3\u11EA\u11EC\u11EF\u11F1\u11F8\u1200\u18A8\u207F\u2124" +
comment|//            "\u2128\u2128\u212C\u212D\u212F\u217F\u2183\u3006\u3038\u303A" +
comment|//            "\u3131\u4DB5\uA000\uA48C\uF900\uFFDC";
comment|//    public static final String NMSTRT_CATEGORIES = "LlLuLoLtNl";
comment|//    public static final String NMCHAR_INCLUDES =
comment|//            "\u002D\u002E\u003A\u005F\u00B7\u0387\u06dd\u212E"; // MHK: added 06dd
comment|//    public static final String NMCHAR_EXCLUDE_RANGES =
comment|//            "\u00AA\u00B5\u00BA\u00BA\u0132\u0133\u013F\u0140\u0149\u0149" +
comment|//            "\u017F\u017F\u01C4\u01CC\u01F1\u01F3\u01F6\u01F9\u0218\u0233" +
comment|//            "\u02A9\u02B8\u02E0\u02EE\u0346\u034E\u0362\u037A\u03D7\u03D7" +
comment|//            "\u03DB\u03DB\u03DD\u03DD\u03DF\u03DF\u03E1\u03E1\u0400\u0400" +
comment|//            "\u040D\u040D\u0450\u0450\u045D\u045D\u0488\u048F\u04EC\u04ED" +
comment|//            "\u0587\u0587\u0653\u0655\u06B8\u06B9\u06BF\u06BF\u06CF\u06CF" +
comment|//            "\u06FA\u07B0\u0950\u0950\u0AD0\u0AD0\u0D82\u0DF3\u0E2F\u0E2F" +
comment|//            "\u0EAF\u0EAF\u0EDC\u0F00\u0F6A\u0F6A\u0F96\u0F96\u0FAE\u0FB0" +
comment|//            "\u0FB8\u0FB8\u0FBA\u1059\u1101\u1101\u1104\u1104\u1108\u1108" +
comment|//            "\u110A\u110A\u110D\u110D\u1113\u113B\u113D\u113D\u113F\u113F" +
comment|//            "\u1141\u114B\u114D\u114D\u114F\u114F\u1151\u1153\u1156\u1158" +
comment|//            "\u1162\u1162\u1164\u1164\u1166\u1166\u1168\u1168\u116A\u116C" +
comment|//            "\u116F\u1171\u1174\u1174\u1176\u119D\u119F\u11A2\u11A9\u11AA" +
comment|//            "\u11AC\u11AD\u11B0\u11B6\u11B9\u11B9\u11BB\u11BB\u11C3\u11EA" +
comment|//            "\u11EC\u11EF\u11F1\u11F8\u1200\u18A9\u207F\u207F\u20DD\u20E0" +
comment|//            "\u20E2\u2124\u2128\u2128\u212C\u212D\u212F\u217F\u2183\u2183" +
comment|//            "\u3006\u3006\u3038\u303A\u3131\u4DB5\uA000\uA48C\uF900\uFFDC";
comment|//    public static final String NMCHAR_CATEGORIES = "LlLuLoLtNlMcMeMnLmNd";
comment|// end of generated code
specifier|public
specifier|static
specifier|final
name|char
name|UNICODE_3_1_ADD_Lu
init|=
literal|'\u03F4'
decl_stmt|;
comment|// added in 3.1
specifier|public
specifier|static
specifier|final
name|char
name|UNICODE_3_1_ADD_Ll
init|=
literal|'\u03F5'
decl_stmt|;
comment|// added in 3.1
comment|// 3 characters changed from No to Nl between 3.0 and 3.1
specifier|public
specifier|static
specifier|final
name|char
name|UNICODE_3_1_CHANGE_No_to_Nl_MIN
init|=
literal|'\u16EE'
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|char
name|UNICODE_3_1_CHANGE_No_to_Nl_MAX
init|=
literal|'\u16F0'
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CATEGORY_Pi
init|=
literal|"\u00AB\u2018\u201B\u201C\u201F\u2039"
decl_stmt|;
comment|// Java doesn't know about category Pi
specifier|public
specifier|static
specifier|final
name|String
name|CATEGORY_Pf
init|=
literal|"\u00BB\u2019\u201D\u203A"
decl_stmt|;
comment|// Java doesn't know about category Pf
block|}
end_class

begin_comment
comment|//
end_comment

begin_comment
comment|// The contents of this file are subject to the Mozilla Public License Version 1.0 (the "License");
end_comment

begin_comment
comment|// you may not use this file except in compliance with the License. You may obtain a copy of the
end_comment

begin_comment
comment|// License at http://www.mozilla.org/MPL/
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// Software distributed under the License is distributed on an "AS IS" basis,
end_comment

begin_comment
comment|// WITHOUT WARRANTY OF ANY KIND, either express or implied.
end_comment

begin_comment
comment|// See the License for the specific language governing rights and limitations under the License.
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// The Original Code is: all this file
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// The Initial Developer of the Original Code is Michael H. Kay.
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// Contributor(s):
end_comment

begin_comment
comment|//
end_comment

end_unit

