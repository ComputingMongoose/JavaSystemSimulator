use16
start:

mov sp,160

; sub word tests
mov ax,00001h
mov bx,00002h
sub ax,bx
mov word[0],ax
mov word[2],bx
pushf				; 158

mov dx,0ffffh
mov word[4],0ffffh
sub word[4],dx
mov word[6],dx
pushf				; 156

mov cx,0ffffh
mov word[8],00001h
sub cx,word[8]
mov word[10],cx
pushf				; 154

mov ax,08000h
sub ax,00001h
mov word[12],ax
pushf				; 152

mov bp,08000h
db 083h,0edh,0ffh
mov word[14],bp
pushf				; 150

mov si,07f81h
sub si,0903ch
mov word[16],si
pushf				; 148

mov word[18],0efc3h
sub word[18],0c664h
pushf				; 146

mov word[20],0e933h
dw 02e83h, 00014h
db 064h
pushf				; 144

; sub byte tests
mov byte[22],001h
sub byte[22],002h
pushf				; 142

mov dh,0ffh
sub dh,0ffh
mov word[23],dx
pushf				; 140

mov al,0ffh
sub al,001h
mov word[25],ax
pushf				; 138

mov byte[27],080h
mov ch,001h
sub ch,byte[27]
mov word[28],cx
pushf				; 136

mov bl,080h
mov byte[30],07fh
sub byte[30],bl
mov word[31],bx
pushf				; 134

mov al,0bch
mov ah,08eh
sub ah,al
mov word[33],ax
pushf				; 132

; sbb word tests
mov ax,00001h
mov bx,00002h
sbb bx,ax
mov word[35],ax
mov word[37],bx
pushf				; 130

mov dx,0ffffh
mov word[39],0ffffh
sbb word[39],dx
mov word[41],dx
pushf				; 128

mov cx,0ffffh
mov word[43],00001h
sbb cx,word[43]
mov word[45],cx
pushf				; 126

mov ax,08000h
sbb ax,00001h
mov word[47],ax
pushf				; 124

mov bp,08000h
db 083h,0ddh,0ffh
mov word[49],bp
pushf				; 122

mov si,052c3h
sbb si,0e248h
mov word[51],si
pushf				; 120

mov word[53],0e74ch
sbb word[53],022c0h
pushf				; 118

mov word[55],0fd85h
dw 01e83h, 00037h
db 0f5h
pushf				; 116

; sbb byte tests
mov byte[57],001h
sbb byte[57],002h
pushf				; 114

mov dh,0ffh
sbb dh,0ffh
mov word[58],dx
pushf				; 112

mov al,0ffh
sbb al,001h
mov word[60],ax
pushf				; 110

mov byte[62],080h
mov ch,001h
sbb ch,byte[62]
mov word[63],cx
pushf				; 108

mov bl,080h
mov byte[65],0ffh
sbb byte[65],bl
mov word[66],bx
pushf				; 106

mov al,0b9h
mov ah,0d3h
sbb ah,al
mov word[68],ax
pushf				; 104

; dec word tests
mov di,00000h
dec di
mov word[70],di
pushf				; 102

mov bp,08000h
db 0ffh, 0cdh
mov word[72],bp
pushf				; 100

mov word[74],07412h
dec word[74]
pushf				; 98

; dec byte tests
mov dl,000h
dec dl
mov word[76],dx
pushf				; 96

mov byte[77],080h
dec byte[77]
pushf				; 94

mov byte[78],0b5h
dec byte[78]
pushf				; 92
hlt

rb 65520-$
jmp start
rb 65535-$
db 0ffh

