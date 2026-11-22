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
    .limit locals 30

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
    iconst_0
    istore 6 ; cociente
    iconst_0
    istore 7 ; residuo
    dconst_0
    dstore 8 ; bono
    dconst_0
    dstore 10 ; tasa
    dconst_0
    dstore 12 ; subtotal
    dconst_0
    dstore 14 ; iva
    dconst_0
    dstore 16 ; t9
    dconst_0
    dstore 18 ; totalPagar
    iconst_0
    istore 20 ; edad
    iconst_0
    istore 21 ; tieneBeca
    iconst_0
    istore 22 ; t11
    iconst_0
    istore 23 ; t12
    iconst_0
    istore 24 ; t13
    iconst_0
    istore 25 ; t14
    iconst_0
    istore 26 ; t15
    iconst_0
    istore 27 ; contador
    iconst_0
    istore 28 ; t16
    iconst_0
    istore 29 ; t18

    ; Codigo traducido desde C3D
L1:
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "=== PRUEBA INTEGRAL DE LOGICA, CICLOS Y OPTIMIZADOR ==="
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
    ; [Guardia C3D] if 4 == 0 then error "Division by zero"
    ldc 20
    ldc 4
    idiv
    istore 6
    ; [Guardia C3D] if 4 == 0 then error "Division by zero"
    ldc 21
    ldc 4
    irem
    istore 7
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Division 20/4 (debe ser 5): "
    invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload 6
    invokevirtual java/io/PrintStream/println(I)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Modulo 21%4 (debe ser 1): "
    invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload 7
    invokevirtual java/io/PrintStream/println(I)V
    ldc2_w 12.0
    dstore 8
    ldc2_w 0.16
    dstore 10
    ldc2_w 200.0
    dstore 12
    dload 12
    dload 10
    dmul
    dstore 14
    dload 12
    dload 14
    dadd
    dstore 16
    dload 16
    dload 8
    dsub
    dstore 18
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Total con IVA menos bono de 12 (debe ser 220.0): "
    invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    dload 18
    invokevirtual java/io/PrintStream/println(D)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Ingresa tu edad: "
    invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
    ; Leer 'edad' (int) desde consola
    aload 1
    invokevirtual java/util/Scanner/nextLine()Ljava/lang/String;
    invokevirtual java/lang/String/trim()Ljava/lang/String;
    invokestatic java/lang/Integer/parseInt(Ljava/lang/String;)I
    istore 20
    iconst_1
    istore 21
    iload 20
    ldc 18
    if_icmpge L_CMP_T_1
    iconst_0
    goto L_CMP_E_1
L_CMP_T_1:
    iconst_1
L_CMP_E_1:
    istore 22
    iload 20
    ldc 25
    if_icmple L_CMP_T_2
    iconst_0
    goto L_CMP_E_2
L_CMP_T_2:
    iconst_1
L_CMP_E_2:
    istore 23
    iload 22
    iload 23
    iand
    istore 24
    iload 21
    iconst_1
    ixor
    istore 25
    iload 24
    iload 25
    ior
    istore 26
    iload 26
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
    ldc 1
    istore 27
L4:
    iload 27
    ldc 3
    if_icmple L_CMP_T_3
    iconst_0
    goto L_CMP_E_3
L_CMP_T_3:
    iconst_1
L_CMP_E_3:
    istore 28
    iload 28
    ifeq L5
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Iteracion while: "
    invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload 27
    invokevirtual java/io/PrintStream/println(I)V
    iload 27
    ldc 1
    iadd
    istore 27
    goto L4
L5:
    iload 27
    istore 29
    iload 29
    ldc 4
    if_icmpeq L6
    goto L7
L6:
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "When correcto: contador llego a 4."
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    goto L8
L7:
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "When: valor inesperado."
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
L8:
    return
.end method
