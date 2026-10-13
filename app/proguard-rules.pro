# Правила ProGuard/R8 для релизной сборки.
# Приложение не использует reflection, поэтому minify пока выключен (isMinifyEnabled = false).
# Правила оставлены как заготовка для следующей итерации (см. бэклог в отчёте).
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
