<?php

$img = imagecreatefrompng("literki4.png");

$literki = [
	['a','�','b','c','�','d','e','�','f','g','h','i','j','k','l','�','m','n','o','�','p','q','r','s','�','t','u','w','x','y','z','�','�','A','�','B','C','�','D','E','�','F','G','H','I','J','K','L','�','M','N','O','�','V'],
	['P','Q','R','S','�','T','U','W','X','Y','Z','�','�','0','1','2','3','4','5','6','7','8','9','0','nawias_l','nawias_p','kwad_l','kwad_p','klam_l','klam_p','srednik','bslash','apost','cudz','mnie','wiecej','przeci','kropa','zapyt','gwiazd','malpa','hash','dolar','proce','dasze','and','gwiazd','plus','minus','slash','bslash','pipe','rownasie','dwukropek','v','wykrzyknik']
];

$h = imagesy($img);
$w = imagesx($img);
echo $w.' '.$h.PHP_EOL;

$linia = 0;
$litera = 0;

function findstart($x, $y1, $y2) {
	global $img, $w;
	for ($i=$x; $i<$w; $i++) {
		for ($j=$y1; $j<$y2; $j++) {
			//echo $i.' '.$j.PHP_EOL;
			if (imagecolorat($img, $i, $j) != 0) 
				return $i;
		}
	}
	return false;
}

function findend($x, $y1, $y2) {
	global $img, $w;
	for ($i=$x; $i<$w; $i++) {
		$ok = true;
		for ($j=$y1; $j<$y2; $j++) {
			//echo $i.' '.$j.PHP_EOL;
			if (imagecolorat($img, $i, $j) != 0) 
				$ok = false;
		}
		if ($ok) return $i;
	}
	return false;
}

function processline($basey) {
	global $img, $linia, $literki, $litera, $w;
	echo "Linia: ".$basey.PHP_EOL;
	$x1 = 2;
	while ($x1 = findstart($x1, $basey-180, $basey+70)) {
		$x2 = findend($x1+1, $basey-180, $basey+70);  
		$lit = $linia.'_'.$litera.'_'.$literki[$linia][$litera++];
		
		$w_ = $x2 - $x1 + 1;
		echo $lit.' '.$x1.' '.$x2.' => '.$w_.PHP_EOL;
		
		$dest = imagecreatetruecolor($w_ + 20, 250);
		imagecopy($dest, $img, 10, 0, $x1-1, $basey-180, $w_, 250);
		
		imageline($dest, 0, 249, $w_+20, 249, 0xFF0000);

		imagepng($dest, $lit.'.png');
		imagedestroy($dest);
		
		$x1 = $x2+1;
	}
}

for ($i=0; $i<$h; $i++) {
	if (imagecolorat($img, 0, $i) != 0) {
		$litera=0;
		processline($i);
		$linia++;
	}
}

//int imagecolorat ( resource $image , int $x , int $y )