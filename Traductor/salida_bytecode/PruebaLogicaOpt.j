.class public PruebaLogicaOpt
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
    .limit locals 22

    ; Inicializacion de java.util.Scanner(System.in)
    new java/util/Scanner
    dup
    getstatic java/lang/System/in Ljava/io/InputStream;
    invokespecial java/util/Scanner/<init>(Ljava/io/InputStream;)V
    astore 1

    ; Pre-inicializacion segura de variables locales
    iconst_0
    istore 2 ; base
    iconst_0
    istore 3 ; t3
    iconst_0
    istore 4 ; t4
    iconst_0
    istore 5 ; formula
    dconst_0
    dstore 6 ; tasa
    dconst_0
    dstore 8 ; subtotal
    dconst_0
    dstore 10 ; iva
    dconst_0
    dstore 12 ; totalPagar
    iconst_0
    istore 14 ; edad
    iconst_0
    istore 15 ; tieneBeca
    iconst_0
    istore 16 ; t8
    iconst_0
    istore 17 ; t9
    iconst_0
    istore 18 ; t10
    iconst_0
    istore 19 ; t11
    iconst_0
    istore 20 ; t12
    iconst_0
    istore 21 ; t13

    ; Codigo traducido desde C3D
L1:
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "=== PRUEBA DE LOGICA, DECIMALES Y OPTIMIZADOR ==="
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    ldc 25
    istore 2
    iload 2
    ldc 25
    iadd
    istore 3
    ldc 6
    ldc 4
    isub
    istore 4
    iload 3
    iload 4
    imul
    istore 5
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Resultado formula (debe ser 100): "
    invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload 5
    invokevirtual java/io/PrintStream/println(I)V
    ldc2_w 0.16
    dstore 6
    ldc2_w 200.0
    dstore 8
    dload 8
    dload 6
    dmul
    dstore 10
    dload 8
    dload 10
    dadd
    dstore 12
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Total con IVA (debe ser 232.0): "
    invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    dload 12
    invokevirtual java/io/PrintStream/println(D)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Ingresa tu edad: "
    invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
    ; Leer 'edad' (int) desde consola
    aload 1
    invokevirtual java/util/Scanner/nextLine()Ljava/lang/String;
    invokevirtual java/lang/String/trim()Ljava/lang/String;
    invokestatic java/lang/Integer/parseInt(Ljava/lang/String;)I
    istore 14
    iconst_1
    istore 15
    iload 14
    ldc 18
    if_icmpge L_CMP_T_1
    iconst_0
    goto L_CMP_E_1
L_CMP_T_1:
    iconst_1
L_CMP_E_1:
    istore 16
    iload 14
    ldc 25
    if_icmple L_CMP_T_2
    iconst_0
    goto L_CMP_E_2
L_CMP_T_2:
    iconst_1
L_CMP_E_2:
    istore 17
    iload 16
    iload 17
    iand
    istore 18
    iload 15
    iconst_1
    ixor
    istore 19
    iload 18
    iload 19
    ior
    istore 20
    iload 20
    ifne L2
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Aplica para tarifa general."
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    goto L3
L2:
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Aplica para tarifa joven."
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
L3:
    iload 15
    iconst_1
    ixor
    istore 21
    iload 21
    ifne L4
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Beca activa confirmada."
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    goto L5
L4:
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Sin beca activa."
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
L5:
    return
.end method
