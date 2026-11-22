.class public PruebaCiclos
.super java/lang/Object

; Constructor por defecto
.method public <init>()V
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

; Punto de entrada principal
.method public static main([Ljava/lang/String;)V
    .limit stack 16
    .limit locals 13

    ; Inicializacion de java.util.Scanner(System.in)
    new java/util/Scanner
    dup
    getstatic java/lang/System/in Ljava/io/InputStream;
    invokespecial java/util/Scanner/<init>(Ljava/io/InputStream;)V
    astore 1

    ; Pre-inicializacion segura de variables locales
    iconst_0
    istore 2 ; suma
    iconst_0
    istore 3 ; i
    iconst_0
    istore 4 ; t1
    iconst_0
    istore 5 ; intentos
    iconst_0
    istore 6 ; secreto
    iconst_0
    istore 7 ; intento
    iconst_0
    istore 8 ; t4
    iconst_0
    istore 9 ; t5
    iconst_0
    istore 10 ; k
    iconst_0
    istore 11 ; t6
    iconst_0
    istore 12 ; t7

    ; Codigo traducido desde C3D
L1:
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "=== 1. CICLO FOR (Rango 1..6 -> itera del 1 al 5) ==="
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    ldc 0
    istore 2
    ldc 1
    istore 3
L2:
    iload 3
    ldc 6
    if_icmplt L_CMP_T_1
    iconst_0
    goto L_CMP_E_1
L_CMP_T_1:
    iconst_1
L_CMP_E_1:
    istore 4
    iload 4
    ifeq L3
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Sumando i = "
    invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload 3
    invokevirtual java/io/PrintStream/println(I)V
    iload 2
    iload 3
    iadd
    istore 2
    iload 3
    ldc 1
    iadd
    istore 3
    goto L2
L3:
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Suma total del for (debe ser 15): "
    invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload 2
    invokevirtual java/io/PrintStream/println(I)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "=== 2. CICLO DO-WHILE (Al menos una ejecucion) ==="
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    ldc 0
    istore 5
    ldc 7
    istore 6
    ldc 0
    istore 7
L4:
    iload 5
    ldc 1
    iadd
    istore 5
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Adivina el numero secreto (pista: es 7): "
    invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
    ; Leer 'intento' (int) desde consola
    aload 1
    invokevirtual java/util/Scanner/nextLine()Ljava/lang/String;
    invokevirtual java/lang/String/trim()Ljava/lang/String;
    invokestatic java/lang/Integer/parseInt(Ljava/lang/String;)I
    istore 7
    iload 7
    iload 6
    if_icmpne L_CMP_T_2
    iconst_0
    goto L_CMP_E_2
L_CMP_T_2:
    iconst_1
L_CMP_E_2:
    istore 8
    iload 8
    ifeq L5
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Incorrecto, intenta de nuevo."
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
L5:
    iload 7
    iload 6
    if_icmpne L_CMP_T_3
    iconst_0
    goto L_CMP_E_3
L_CMP_T_3:
    iconst_1
L_CMP_E_3:
    istore 9
    iload 9
    ifne L4
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Adivinaste en intentos: "
    invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload 5
    invokevirtual java/io/PrintStream/println(I)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "=== 3. CICLO WHILE CON BREAK ==="
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    ldc 1
    istore 10
L6:
    iload 10
    ldc 10
    if_icmple L_CMP_T_4
    iconst_0
    goto L_CMP_E_4
L_CMP_T_4:
    iconst_1
L_CMP_E_4:
    istore 11
    iload 11
    ifeq L7
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Valor de k: "
    invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload 10
    invokevirtual java/io/PrintStream/println(I)V
    iload 10
    ldc 4
    if_icmpeq L_CMP_T_5
    iconst_0
    goto L_CMP_E_5
L_CMP_T_5:
    iconst_1
L_CMP_E_5:
    istore 12
    iload 12
    ifeq L8
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Se alcanzo k = 4, ejecutando break..."
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    goto L7
L8:
    iload 10
    ldc 1
    iadd
    istore 10
    goto L6
L7:
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Ciclo while terminado correctamente."
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    return
.end method
