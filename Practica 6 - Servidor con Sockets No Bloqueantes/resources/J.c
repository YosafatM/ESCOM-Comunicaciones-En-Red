#include <stdio.h>
#include <stdlib.h>

int factorial(int n);

int main(int argc, char *argv[]) {
	char opc = 's';
	while(opc == 's'){
		int c = 0, n = 0, k = 0, nfac = 0, kfac = 0, nmk = 0, nmkfac = 0;
		
		printf("Cuantos elementos son en total? (n) \n");
		scanf("%d", &n);
		printf("Cuantos elementos conforman su muestra? (k) \n");
		scanf("%d", &k);
		
		nmk = n - k;
		nfac = factorial(n);
		kfac = factorial(k);
		nmkfac = factorial(nmk);
		
		c = (nfac)/((kfac)*(nmkfac));
		
		printf("El numero de combinaciones posibles es: %d \n", c);
		
		printf("Quiere volver a utilizar el programa? \n");
		printf("Si -----> s \n");
		printf("No -----> n \n");
		scanf(" %c", &opc);
	}
	
	return 0;
}

int factorial(int n){
	if(n == 0){
		return 1;
	}else{
		return factorial(n-1) * n;
	}
}
