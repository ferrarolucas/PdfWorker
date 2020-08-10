package com.pdfutil;

public class RectangleModel {

	public int page;
	
	public int x;
	public int y;
	
	public int height;
	public int width;
	
	public RectangleModel() { }
	
	public ErrorModel validate() {
		if(this.page <= 0) {
			return new ErrorModel("A p�gina da assinatura precisa ser maior que 0.");
		}

		if(this.x < 0) {
			return new ErrorModel("A posi��o x do ret�ngulo da assinatura precisa ser maior que 0.");
		}

		if(this.y < 0) {
			return new ErrorModel("A posi��o y do ret�ngulo da assinatura precisa ser maior que 0.");
		}

		if(this.height < 0) {
			return new ErrorModel("A altura do ret�ngulo da assinatura precisa ser maior que 0.");
		}

		if(this.width < 0) {
			new ErrorModel("A largura do ret�ngulo da assinatura precisa ser maior que 0.");
		}
		
		return null;
	}
}
