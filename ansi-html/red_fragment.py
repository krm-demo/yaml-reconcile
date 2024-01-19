import os
from pathlib import Path
from rich.console import Console
from rich.text import Text

console: Console = Console(width=120, record=True)
current_script_path: Path = Path(__file__)

cmd_echo: str = 'echo -e "This is a \\x1b[31mred fragment\\x1b[39m of text"'
res_echo: str = os.popen(f"bash -c '{cmd_echo}'").read()
console.print(f"... > {cmd_echo}")
console.print(Text.from_ansi(res_echo))
console.print()

txt_file_name = current_script_path.with_suffix('.txt').name
cmd_echo_to_file: str = f"{cmd_echo} > {txt_file_name}"
cmd_cat_file: str = f"cat {txt_file_name}"
res_echo_and_cat: str = os.popen(f"bash -c '{cmd_echo_to_file}'").read() + os.popen(cmd_cat_file).read()
console.print(f"... > {cmd_echo_to_file}")
console.print(f"... > {cmd_cat_file}")
console.print(Text.from_ansi(res_echo_and_cat))
console.print()

cmd_hexdump: str = f"hexdump -C {txt_file_name}"
res_hexdump: str = os.popen(cmd_hexdump).read()
console.print(f"... > {cmd_hexdump}")
console.print(Text.from_ansi(res_hexdump))
print()

red_fragment_svg_file_name = current_script_path.with_suffix('.svg').name
red_fragment_title = "red fragment with ESC-symbol '\\x1b'"
console.save_svg(red_fragment_svg_file_name, title=red_fragment_title)
print(f"output is captured and saved in '{red_fragment_svg_file_name}'")

# echo "System.out.println(\"$JAVA_HOME\");" | jshell --feedback concise | sed -n '2p'
