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
			return new ErrorModel("A página da assinatura precisa ser maior que 0.");
		}

		if(this.x < 0) {
			return new ErrorModel("A posição x do retângulo da assinatura precisa ser maior que 0.");
		}

		if(this.y < 0) {
			return new ErrorModel("A posição y do retângulo da assinatura precisa ser maior que 0.");
		}

		if(this.height < 0) {
			return new ErrorModel("A altura do retângulo da assinatura precisa ser maior que 0.");
		}

		if(this.width < 0) {
			new ErrorModel("A largura do retângulo da assinatura precisa ser maior que 0.");
		}
		
		return null;
	}
}
