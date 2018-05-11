<?php

// ciągu znaków które mają być ukrywane należy otoczyć przez ' ' a nie " "

function cleanup($dir, $res2 = false) {
	foreach (glob($dir.'*') as $f) {
		echo $f.PHP_EOL;
		if (strlen($f) > strlen($dir)+2) {
			if (is_dir($f)) {
				if (!$res2 && basename($f) == 'res2') {
					cleanup($f.'\\', true);
					echo "Kasuje RES2: ".$f.PHP_EOL;	
					rmdir($f);
				} else {
					cleanup($f.'\\', $res2);
					if ($res2) {
						echo "Kasuje katalog w RES2: ".$f.PHP_EOL;	
						rmdir($f);
					}
				}
			}
		}
		if ($res2 && substr($f, -4) == '.xml') {
			echo "Kasuje w RES2: ".$f.PHP_EOL;
			unlink($f);
			continue;
		}
		if (substr($f, -4) == '.org') {
			echo "Kasuje ORG: ".$f.PHP_EOL;
			unlink($f);
			continue;
		}	
		if (preg_match('/\\_[a-z0-9]{10}/', $f)) {
			echo "Kasuje: ".$f.PHP_EOL;
			unlink($f);
			continue;
		}
	}
}

cleanup(__DIR__.'\\');
