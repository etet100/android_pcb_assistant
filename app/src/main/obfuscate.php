<?php

// ciągu znaków które mają być ukrywane należy otoczyć przez ' ' a nie " "

echo substr(md5('fef0a3d4'.'e3e4e97b39bbd30aece3d6703de3b52cca00d70f17170412bc35a48c3668ab91'),0, 8).PHP_EOL;
echo substr(md5('62183e25'.'e3e4e97b39bbd30aece3d6703de3b52cca00d70f17170412bc35a48c3668ab91'),0, 8).PHP_EOL;
//die();

define('COMMENTS', 0);
define('OBFUSCATE', 0);

define('DELIMITER', "\\/ \"\(\){}\\n\\r\\t\<\>\=\,\*\!\-\`\;\'\+\:\?\@\[\]\.");
$tags = $processed = [];

include "obfuscate_keep.php";
include "obfuscate_change.php";

$translation = [
	'EagleView_type'=>'_3fa9a2fa15_type',
	'Java_bts_pcbassistant_parsing_BoardParser_Native'=>'Java_bts_pcbassistant_parsing__17d44f6bb4e_Native',
	'Java_bts_pcbassistant_parsing_SchematicParser_Native'=>'Java_bts_pcbassistant_parsing__1646d76c092_Native',
];
$keep_translation = [
];
uasort($keep, function($a,$b) {
	return strlen($b) - strlen($a);
});

//czy aktualny fragment nie jest otoczony przez > < lub " ", jeśli tak to true
function check_surrounding_chars(array &$parts, &$count, &$current_index) {
	$force = false;
	if ($current_index > 1 && $current_index < $count - 1) {
		$l2 = substr($parts[$current_index-2], -3);
		if ($l2 == '^O^')
			$force = true;
	}
	if (!$force && $current_index > 0 && $current_index < $count - 1) {
		$l = substr($parts[$current_index-1], -1);
		$r = substr($parts[$current_index+1], 0, 1);
		if ($l == '"' && $r == '"') 
			return true;
		if ($l == '>' && $r == '<') 
			return true;
	}
	//można podmieniać
	return false;
} 

function processFile($file, $file_org) {

	global $tags, $keep, $change, $translation, $keep_translation;	
	
	if (!file_exists($file_org)) {
		//echo "Brak: ".$file_org.PHP_EOL;
		//die();
		file_put_contents($file_org, file_get_contents($file));
	}
	echo $file.PHP_EOL;
	
	$f_org = file_get_contents($file_org);
	
	if (OBFUSCATE == 1)
	{
	
		foreach ($keep as $t) {
		//	echo $t.' => '.base64_encode($t).PHP_EOL;

			if (array_key_exists($t, $keep_translation)) {
				$t2 = $keep_translation[$t];
			} else {
				$keep_translation[$t] = $t2 = substr(md5($t), 0, 16);
			}
			
			$f_org = preg_replace('/(['.DELIMITER.'])'.preg_quote($t).'(['.DELIMITER.'])/Ums', '$1^KEEP^'.$t2.'^$2', $f_org);
			//		$f_org = str_replace($t, '^KEEP^'.$t2.'^', $f_org);
		}		
		
		//wymuszenie obfuscate java oraz cpp
		$f_org = str_replace('/*obf*/', '^O^', $f_org);		
		
		//komentarze
		$f_org = preg_replace('/\/\*.*\*\//Ums', '', $f_org);
		$f_org = preg_replace('/([^:])\/\/.*$/Um', '$1', $f_org); // wyklucz przypadki ://
		
		//liczby
		$f_org = preg_replace_callback('/([\d\.]{3,10}(f|d))/m', function($r) {
			return str_replace('.', '^dot^', $r[1]);
		}, $f_org);
		
		//define('DELIMITER', "\r\n");
		//var_dump(DELIMITER);	
		$parts = preg_split('/(['.DELIMITER.']+)/', $f_org, -1, PREG_SPLIT_DELIM_CAPTURE);// | PREG_SPLIT_OFFSET_CAPTURE);
		$delim = true;
		$f_new = '';
		
		if ($file == 'C:\Android\PCBAssistant\app\src\main/jni/native-parser.cpp') {
			//print_r($parts);
		}
		//print_r($parts);
		$parts_count = count($parts);
		foreach ($parts as $i=>$k) {
			$delim = !$delim;
			if ($delim || strlen($k) < 3 || in_array($k, $keep)) {
				$f_new .= $k;
				continue;
			}
			if (!in_array($k, $tags)) {
				$tags[] = $k;	
			}
			$t = str_replace(
				['^dot^'],
				['.'],
				$k);			
			if (substr($t, -1) == '_') {
				$underscore = true;
				$t = substr($t, 0, -1);
			} else
				$underscore = false;

			if (in_array($t, $change) && !check_surrounding_chars($parts, $parts_count, $i)) {
				if (array_key_exists($t, $translation)) {
					$t2 = $translation[$t];
				} else {
					$translation[$t] = $t2 = '_'.substr(md5($t), 0, 10);
				}
				$f_new .= $t2.($underscore?"_":"");
				if (COMMENTS) {
					$f_new .= ' /* '.$t.($underscore?"_":"").' */ ';
				}
			} else {
				if (substr($t, 0, 6) == '^KEEP^') {
					$f_new .= $t;
				} else {
					//przywróć usunięty _ na końcu !
					if ($underscore) {
						$t .= '_';
					}
					$f_new .= $t;
					if (COMMENTS) {
						//$f_new .= ' /* t */ ';
					}
				}
			}
		}	

		$f_new = preg_replace_callback('/\^KEEP\^(.*)\^/UmS', function($r) use ($keep_translation) {
			return array_search($r[1], $keep_translation);
		}, $f_new);
		
		$f_new = str_replace("^O^", "", $f_new);
		
	} else {
		$f_new = $f_org;
	}
		
	file_put_contents($file, $f_new);
	
	if (OBFUSCATE == 1)
	{
	
		$fname = pathinfo($file, PATHINFO_FILENAME);
		if (array_key_exists($fname, $translation)) {
			$renameTo = str_replace($fname, $translation[$fname], $file);
			echo "Rename: ".$renameTo.PHP_EOL;
			
			rename($file, $renameTo);
		}
		
	}
}

function clean($dir) {
	foreach (glob($dir.'*') as $f) {
		if (is_dir($f)) {
			clean($f.'/');
		} else {
			if (substr(basename($f), 0, 1) == '_') {
				echo "Del: ".$f.PHP_EOL;
				switch (pathinfo($f, PATHINFO_EXTENSION)) {
					case 'java':
					case 'org':
					//case 'xml':
						unlink($f);
						break;
				}
			}
		}	
	}
}
clean(__DIR__.'/');
/*
function copy($dir) {
	global $processed;
	foreach (glob($dir.'*') as $f) {
		if (is_dir($f)) {
			if (basename($f) == 'com' || basename($f) == 'de')
				continue;
			if (basename($dir) == 'res') {
				@mkdir(str_replace('/res/', '/res2/', $dir));
				@mkdir(str_replace('/res/', '/res2/', $f));
			}
			copy($f.'/');
		} else {
			if (substr(pathinfo($file, PATHINFO_FILENAME), 0, 1) == '_')
				continue;
			switch (pathinfo($f, PATHINFO_EXTENSION)) {
				case 'java':
					processFile($f, $f.'.org');
					break;
				case 'xml':
					processFile($f, str_replace('/res/', '/res2/', $f));
					break;
				case 'org': //tylko java
					$f2 = substr($f, 0, -4);
					if (!file_exists($f2)) {
						//brakuje pliku oryginalnego (bez org), przetwarzaj mimo wszystko
						echo "Brak pliku oryginalnego: ".$f.PHP_EOL;
						processFile($f2, $f);
					}
					break;
			}
		}	
	}
}
*/
if (in_array("copy", $argv)) {
	scan(__DIR__.'/');
	die();
}

if (in_array("revert", $argv)) {
}

function scan($dir) {
	global $processed;
	foreach (glob($dir.'*') as $f) {
		if (is_dir($f)) {
			if (basename($f) == 'com' || basename($f) == 'de' || basename($f) == 'res2')
				continue;
			if (basename($dir) == 'res') {
				@mkdir(str_replace('/res/', '/res2/', $dir));
				@mkdir(str_replace('/res/', '/res2/', $f));
			}
			scan($f.'/');
		} else {
			if (substr(pathinfo($f, PATHINFO_FILENAME), 0, 1) == '_')
				continue;
			switch (pathinfo($f, PATHINFO_EXTENSION)) {
				case 'cpp':
				case 'java':
					processFile($f, $f.'.org');
					break;
				case 'xml':
					processFile($f, str_replace('/res/', '/res2/', $f));
					break;
				case 'org': //tylko java i cpp
					$f2 = substr($f, 0, -4);
					if (!file_exists($f2)) {
						//brakuje pliku oryginalnego (bez org), przetwarzaj mimo wszystko
						echo "Brak pliku oryginalnego: ".$f2.PHP_EOL;
						processFile($f2, $f);
					}
					if (!OBFUSCATE) {
						unlink($f);
					}
					break;
			}
		}	
	}
}
scan(__DIR__.'/');



//print_r($tags);