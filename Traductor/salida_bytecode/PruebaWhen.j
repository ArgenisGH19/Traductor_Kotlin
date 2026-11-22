.class public PruebaWhen
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
    .limit locals 9

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
    istore 4 ; opcion
    iconst_0
    istore 5 ; res
    iconst_0
    istore 6 ; t1
    iconst_0
    istore 7 ; t5
    iconst_0
    istore 8 ; residuo

    ; Codigo traducido desde C3D
L1:
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "=== PRUEBA DE WHEN (SWITCH) Y ARITMETICA ==="
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Ingresa el primer numero (a): "
    invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
    ; Leer 'a' (int) desde consola
    aload 1
    invokevirtual java/util/Scanner/nextLine()Ljava/lang/String;
    invokevirtual java/lang/String/trim()Ljava/lang/String;
    invokestatic java/lang/Integer/parseInt(Ljava/lang/String;)I
    istore 2
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Ingresa el segundo numero (b): "
    invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
    ; Leer 'b' (int) desde consola
    aload 1
    invokevirtual java/util/Scanner/nextLine()Ljava/lang/String;
    invokevirtual java/lang/String/trim()Ljava/lang/String;
    invokestatic java/lang/Integer/parseInt(Ljava/lang/String;)I
    istore 3
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Elige una operacion:"
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "1. Suma"
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "2. Resta"
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "3. Multiplicacion"
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "4. Division y Modulo"
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Opcion: "
    invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
    ; Leer 'opcion' (int) desde consola
    aload 1
    invokevirtual java/util/Scanner/nextLine()Ljava/lang/String;
    invokevirtual java/lang/String/trim()Ljava/lang/String;
    invokestatic java/lang/Integer/parseInt(Ljava/lang/String;)I
    istore 4
    ldc 0
    istore 5
    iload 4
    istore 6
    iload 6
    ldc 1
    if_icmpeq L2
    iload 6
    ldc 2
    if_icmpeq L3
    iload 6
    ldc 3
    if_icmpeq L4
    iload 6
    ldc 4
    if_icmpeq L5
    goto L8
L2:
    iload 2
    iload 3
    iadd
    istore 5
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Resultado de la suma: "
    invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload 5
    invokevirtual java/io/PrintStream/println(I)V
    goto L9
L3:
    iload 2
    iload 3
    isub
    istore 5
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Resultado de la resta: "
    invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload 5
    invokevirtual java/io/PrintStream/println(I)V
    goto L9
L4:
    iload 2
    iload 3
    imul
    istore 5
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Resultado de la multiplicacion: "
    invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload 5
    invokevirtual java/io/PrintStream/println(I)V
    goto L9
L5:
    iload 3
    ldc 0
    if_icmpne L_CMP_T_1
    iconst_0
    goto L_CMP_E_1
L_CMP_T_1:
    iconst_1
L_CMP_E_1:
    istore 7
    iload 7
    ifne L6
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Error: No se puede dividir entre cero."
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    goto L7
L6:
    ; [Guardia C3D] if b == 0 then error "Division by zero"
    iload 2
    iload 3
    idiv
    istore 5
    ; [Guardia C3D] if b == 0 then error "Division by zero"
    iload 2
    iload 3
    irem
    istore 8
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Cociente: "
    invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload 5
    invokevirtual java/io/PrintStream/println(I)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Residuo: "
    invokevirtual java/io/PrintStream/print(Ljava/lang/String;)V
    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload 8
    invokevirtual java/io/PrintStream/println(I)V
L7:
    goto L9
L8:
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Opcion no valida."
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
L9:
    return
.end method
