# PdfWorker

## CREATE FORM

*http://localhost:8084/PdfWorker/api/createForm*

**POST**

**request**
```json
{
    "pdfB64" : "pdf original sem formulário em base64",
    "fields" : [
        {
            "name" : "2_16_76_1_12_1_1",
            "position" : {
                "page": 1,
                "x": 150,
                "y": 775,
                "height": 20,
                "width": 300
            },
            "value" : "RECEITUÁRIO DE CONTROLE ESPECIAL"
        },
        {
            "name" : "2_16_76_1_4_2_2_1",
            "position" : {
                "page": 1,
                "x": 110,
                "y": 695,
                "height": 10,
                "width": 40
            }
        },
        {
            "name" : "2_16_76_1_4_2_2_2",
            "position" : {
                "page": 1,
                "x": 170,
                "y": 695,
                "height": 10,
                "width": 20
            }
        },
        {
            "name" : "2_16_76_1_4_2_3_1",
            "position" : {
                "page": 1,
                "x": 355,
                "y": 225,
                "height": 10,
                "width": 40
            }
        },
        {
            "name" : "2_16_76_1_4_2_3_2",
            "position" : {
                "page": 1,
                "x": 420,
                "y": 225,
                "height": 10,
                "width": 20
            }
        }
    ],
    "signatureFields" : [
        {
            "name" : "sig_medico",
            "position" : {
                "page": 1,
                "x": 325,
                "y": 275,
                "height": 30,
                "width": 145
            }
        },
        {
            "name" : "sig_farmaceutico",
            "position" : {
                "page": 1,
                "x": 315,
                "y": 190,
                "height": 30,
                "width": 145
            }
        }
    ]
}
```
**response**
```json
{
    "pdfB64": "pdf contendo o formulário em base64"
}
```

**Observações:**
1) fields: é um array de campos a serem adicionados. Estes campos serão do tipo texto e serão adicionados com ou sem conteúdo, dependendo da inclusão do parâmetro "value". Os valores podem ser adicionados nessa api ou na api de inserção da assinatura "fake".
2) signatureFields: é um array com os campos de assinatura. O nome deste campo é um metadado interno, portanto não ficará visível no pdf.
3) todo campo, de texto ou de assinatura, possuem nome e posição. O nome é um metadado usado para identificar o campo e a posição é o retângulo onde esse campo ficará presente no documento. A posição é composta pelas posições X e Y do canto inferior esquerdo, pela altura, largura e página.

========================================================================

## INSERT BLANK SIGNATURE (FORM)

*http://localhost:8084/PdfWorker/api/insertBlankSignatureForm*

**POST**

**request**
```json
{
    "pdfB64": "pdf base64 com formulário",
    "signatureField":{
        "name" : "sig_medico",
        "logoB64" : "logo da assinatura"
    },
    "signerInfo":{
        "signerName" : "Joaquim",
	    "signerText" : "Assinado por Joaquim",
        "signingTime" : "04/07/2020 16:00:00" 
    },
    "fieldsToBlock":[
        {
            "name":"2_16_76_1_12_1_1"
        },
        {
            "name":"2_16_76_1_4_2_2_1",
            "value": "12345"
        },
        {
            "name":"2_16_76_1_4_2_2_2",
            "value": "SC"
        }
    ],
	"blocksWholeForm" : "false"
}
```

**response**
```json
{
    "hashToSignB64": "k5EAXLkbNaj62MGKzpT2jK80yChX91D7JcpuEK6oHC8=",
    "pdfBlankSigned": "pdf com a assinatura em branco em base64"
}
```

**Observações:**
1) blocksWholeForm indica se a assinatura irá bloquear todos os campos do formulário. Ele deve ser false sempre que houver campos para um próximo assinante preencher.
2) após essa API é necessário chamar a API [site_portal]/api/v2/document/create e, após a realização da assinatura, é necessário chamar a api [site_portal]/api/documento/package?chave=[chave_documento]&incluirOriginal=false&incluirManifesto=false&zipped=false

========================================================================

## INSERT BLANK SIGNATURE (META)

*http://localhost:8084/PdfWorker/api/insertBlankSignatureMeta*

**POST**

**request**
```json
{
    "pdfB64": "pdf base64 com formulário",
    "signatureField":{
        "name" : "remetente",
        "position" : {
            "page": 1,
            "x": 325,
            "y": 275,
            "height": 30,
            "width": 145
        },
		"logoB64" : "logo da assinatura",
    },
    "signerInfo":{
        "signerName" : "Joaquim",
	    "signerText" : "Assinado por Joaquim",
        "signingTime" : "04/07/2020 16:00:00" 
    },
    "metadata":[
        {
            "name":"2.16.76.1.12.1.1"
        },
        {
            "name":"2.16.76.1.4.2.2.1",
            "value": "12345"
        },
        {
            "name":"2.16.76.1.4.2.2.2",
            "value": "SC"
        }
    ],
	"addAsDocInfo" : false
}
```

**response**
```json
{
    "hashToSignB64": "k5EAXLkbNaj62MGKzpT2jK80yChX91D7JcpuEK6oHC8=",
    "pdfBlankSigned": "pdf com a assinatura em branco em base64"
}
```

**Observações:**
1) os identificadores dos campos do metadata precisam ser separados por pontos.
2) o campo "addAsDocInfo" indica se os metadados serão adicionados no header/info do documento ou se serão inseridos como metadados XML. Se true, então vai para info/header, se false, então vai para o XML.

========================================================================

## REPLACE CONTENTS ENTRY

*http://localhost:8084/PdfWorker/api/replaceContentsEntry*

**POST**

**request**
```json
{
    "blankSignedPdfB64" :  "pdf com a assinatura em branco em base64",
	"signatureFieldName": "sig_medico",
	"p7sB64": "p7s em base64"
}
```

**response**
```json
{
    "pdfB64": "pdf assinado em base64"
}
```
