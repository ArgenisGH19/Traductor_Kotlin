.class public ProgramaFinal
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
    .limit locals 12

    ; Inicializacion de java.util.Scanner(System.in)
    new java/util/Scanner
    dup
    getstatic java/lang/System/in Ljava/io/InputStream;
    invokespecial java/util/Scanner/<init>(Ljava/io/InputStream;)V
    astore 1

    ; Pre-inicializacion segura de variables locales
    iconst_0
    istore 2 ; a
    iconst_0
    istore 3 ; b
    iconst_0
    istore 4 ; c
    iconst_0
    istore 5 ; t1
    iconst_0
    istore 6 ; d
    iconst_0
    istore 7 ; entrada
    iconst_0
    istore 8 ; total
    iconst_0
    istore 9 ; t4
    iconst_0
    istore 10 ; contador
    iconst_0
    istore 11 ; t5

    ; Codigo traducido desde C3D
L1:
    ldc 5
    istore 2
    ldc 10
    istore 3
    ldc 8
    istore 4
    iload 2
    iload 3
    iadd
    istore 5
    iload 5
    iload 4
    imul
    istore 6
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "=== Prueba de Multiplicacion ==="
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "El valor de d (a * b) es: "
    invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload 6
    invokevirtual java/io/PrintStream/println(I)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Ingresa un numero entero para sumarle a d: "
    invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
    ; Leer 'entrada' (int) desde consola
    aload 1
    invokevirtual java/util/Scanner/nextLine()Ljava/lang/String;
    invokevirtual java/lang/String/trim()Ljava/lang/String;
    invokestatic java/lang/Integer/parseInt(Ljava/lang/String;)I
    istore 7
    iload 6
    iload 7
    iadd
    istore 8
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "El nuevo total es: "
    invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload 8
    invokevirtual java/io/PrintStream/println(I)V
    iload 8
    ldc 150
    if_icmpgt L_CMP_T_1
    iconst_0
    goto L_CMP_E_1
L_CMP_T_1:
    iconst_1
L_CMP_E_1:
    istore 9
    iload 9
    ifne L2
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "El resultado es 150 o menor."
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    goto L3
L2:
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "El resultado supera 150."
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
L3:
    ldc 1
    istore 10
L4:
    iload 10
    ldc 3
    if_icmple L_CMP_T_2
    iconst_0
    goto L_CMP_E_2
L_CMP_T_2:
    iconst_1
L_CMP_E_2:
    istore 11
    iload 11
    ifeq L5
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Iteracion: "
    invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload 10
    invokevirtual java/io/PrintStream/println(I)V
    iload 10
    ldc 1
    iadd
    istore 10
    goto L4
L5:
    return
.end method
