use16
start:
mov sp,208

; Exception 0 handler
mov word[0],01000h
mov word[2],0f000h

mov bp,208

; div word tests
; easy test
mov dx,00h
mov ax,014h
mov bx,05h

mov word[bp],02h
div bx
add bp,02h

mov word[128],ax
mov word[130],bx
mov word[4],dx
pushf					; 206


mov dx,0a320h
mov ax,0c3dah
mov word[6],0ffffh

mov word[bp],04h
div word[6]
add bp,02h

mov word[8],ax
mov word[10],dx
pushf					; 204


mov dx,0ffffh
mov ax,0ffffh
mov cx,01h

mov word[bp],02h
div cx
add bp,02h

mov word[12],ax
mov word[14],cx
mov word[16],dx
pushf					; 202


mov dx,0ffffh
mov ax,0ffffh
mov word[18],0ffffh

mov word[bp],04h
div word[18]
add bp,02h

mov word[20],ax
mov word[22],dx
pushf					; 200


mov dx,0fbb4h
mov ax,0c3dah
mov cx,0ae8eh

mov word[bp],02h
div cx
add bp,02h

mov word[24],ax
mov word[26],cx
mov word[28],dx
pushf					; 198


mov dx,025c9h
mov ax,0f110h

mov word[bp],02h
div ax
add bp,02h

mov word[30],ax
mov word[32],dx
pushf					; 196


; div byte tests
; easy test
mov ax,014h
mov bx,05h

mov word[bp],02h
div bl
add bp,02h

mov word[34],ax
mov word[36],bx
mov word[38],dx
pushf					; 194

mov dx,0a320h
mov ax,0c3dah
mov word[40],0ffh

mov word[bp],04h
div byte[40]
add bp,02h

mov word[42],ax
mov word[44],dx
pushf					; 192

mov ax,0ffffh
mov dh,01h

mov word[bp],02h
div dh
add bp,02h

mov word[46],ax
mov word[48],dx
pushf					; 190

mov ax,0ffffh
mov word[50],0ffffh

mov word[bp],04h
div byte[51]
add bp,02h

mov word[52],ax
mov word[54],dx
pushf					; 188

mov ax,0008ah
mov cx,0ae8eh

mov word[bp],02h
div cl
add bp,02h

mov word[56],ax
mov word[58],cx
pushf					; 186

mov dx,00669h
mov ax,089f3h

mov word[bp],02h
div al
add bp,02h

mov word[60],ax
mov word[62],dx
pushf					; 184

; idiv word tests
; easy test
mov dx,00h
mov ax,014h
mov bx,0fah

mov word[bp],02h
idiv bx
add bp,02h

mov word[64],ax
mov word[66],bx
mov word[68],dx
pushf					; 182


mov dx,0a320h
mov ax,0c3dah
mov word[70],0ffffh

mov word[bp],04h
idiv word[70]
add bp,02h

mov word[72],ax
mov word[74],dx
pushf					; 180


mov dx,0ffffh
mov ax,0ffffh
mov cx,01h

mov word[bp],02h
idiv cx
add bp,02h

mov word[76],ax
mov word[78],cx
mov word[80],dx
pushf					; 178


mov dx,0ffffh
mov ax,0ffffh
mov word[82],0ffffh

mov word[bp],04h
idiv word[82]
add bp,02h

mov word[84],ax
mov word[86],dx
pushf					; 176


mov dx,0fbb4h
mov ax,0c3dah
mov cx,0ae8eh

mov word[bp],02h
idiv cx
add bp,02h

mov word[88],ax
mov word[90],cx
mov word[92],dx
pushf					; 174


mov dx,025c9h
mov ax,0f110h

mov word[bp],02h
idiv ax
add bp,02h

mov word[94],ax
mov word[96],dx
pushf					; 172

; idiv byte tests
; easy test
mov ax,014h
mov bx,05h

mov word[bp],02h
idiv bl
add bp,02h

mov word[98],ax
mov word[100],bx
mov word[102],dx
pushf					; 170


mov dx,0a320h
mov ax,0c3dah
mov word[104],0ffh

mov word[bp],04h
idiv byte[104]
add bp,02h

mov word[106],ax
mov word[108],dx
pushf					; 168


mov ax,0ffffh
mov dh,01h

mov word[bp],02h
idiv dh
add bp,02h

mov word[110],ax
mov word[112],dx
pushf					; 166


mov ax,0ffffh
mov word[114],0ffffh

mov word[bp],04h
idiv byte[115]
add bp,02h

mov word[116],ax
mov word[118],dx
pushf					; 164


mov ax,0008ah
mov cx,0ae8eh

mov word[bp],02h
idiv cl
add bp,02h

mov word[120],ax
mov word[122],cx
pushf					; 162


mov dx,00669h
mov ax,089f3h

mov word[bp],02h
idiv al
add bp,02h

mov word[124],ax
mov word[126],dx
pushf					; 160


; AAM tests
mov ax,0ffffh

mov word[bp],02h
aam 0
add bp,02h
mov word[132],ax
pushf					; 158

mov word[bp],02h
aam 1
add bp,02h
mov word[134],ax
pushf					; 156

mov ax,0ffffh
mov word[bp],02h
aam
add bp,02h
mov word[136],ax
pushf					; 154

mov ax,0ff00h
mov word[bp],02h
aam 0
add bp,02h
mov word[138],ax
pushf					; 152

mov word[bp],02h
aam 1
add bp,02h
mov word[140],ax
pushf					; 150

mov ax,03ffbh
mov word[bp],02h
aam
add bp,02h
mov word[142],ax
pushf					; 148

hlt

; Exception handler (int 0)

; On 8086, 8088, 80186, and 80188 processors, the return address on the stack points at
; the next instruction after the divide instruction. On the 80286 and later processors, the
; return address points at the beginning of the divide instruction (include any prefix bytes
; that appear).
rb 01000h-$
push ax
push di
mov ax,word[bp]
mov si,sp
add si,4
mov si,word[si]
mov word[bp],si
add si,ax              ; should be 0 for 8088, 4 for 286 and later
mov di,sp
add di,4            
mov word[di],si
pop di
pop ax
iret

rb 65520-$
jmp start
rb 65535-$
db 0ffh
