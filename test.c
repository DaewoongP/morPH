#include <avr/io.h>
#define F_CPU 14745600
#define KEY_CTRL_IN	PING
#define KEY_CTRL_OUT	PORTC
#define R0		0x10

unsigned char key_scan(void) {
	unsigned char scan=0;
	unsigned char key_control=0;
	unsigned char input_data=0;
	unsigned char key=0;

	key_control = R0;
	for(scan=0; scan<4; scan++) {		// 4회 반복 (절차 1 ~ 절차 4)
		KEY_CTRL_OUT &= 0x0F;		// 상위 4bit만 강제로 0을 만든다. 
		KEY_CTRL_OUT |= key_control;	// 반드시 key_control의 하위 4bit는 0000임
		_delay_ms(1); 
		input_data = KEY_CTRL_IN & 0x07;
		if(input_data !=0) key = (input_data >>1)+1+scan*3; 
		key_control <<=1;		// <=> key_control=key_control>>1;
	}
	return key;
}

int main(){
	unsigned char led = 0;
	unsigned int key = 0;
	DDRG = 0x00;
	DDRC = 0xFF;
	DDRB = 0xFF;
	
	while(1){
		key = key_scan();
		
		led = 0x01;
		if(key == 0) led = 0x00;
		else if(key < 9) led = led << (key - 1);
		else if(key = 9) led = 0x81;
		else if(key = 10) led = 0x0F;
		else if(key = 11) led = 0xFF;
		else if(key = 12) led = 0xF0;
	}
}