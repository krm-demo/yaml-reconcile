from pathlib import Path
from rich.console import Console

console: Console = Console(width=40, record=True)
content: str  = """\
The first line
The second line
....
....
The last line\
"""

for color in ["blue", "red", "yellow"]:
    console.rule(f"content with {color} color")
    console.print(content, style=f"bold {color}")

current_script_path: Path = Path(__file__)
example_svg_file_name = f"{current_script_path.with_suffix('.svg')}"
console.save_svg(example_svg_file_name, title=f"{current_script_path.name}")

print(". . . . . . . . . . . . . . . . . . . .")
print(f"output is captured and saved in '{example_svg_file_name}'")

short_name = f"{current_script_path.with_suffix('.svg').name}"
print(f"to insert the link to that '.svg' file '.md' document use:\n"
      f"- either '![Alt text](../ansi-html/{short_name})'\n"
      f"- or     '<img src=\"../ansi-html/{short_name}\">'")

# os.system("which python")
# os.system("python --version")
# os.system("java --version")
#
# cmd_python_version: str = "python --version"
# res_python_version: str = os.popen(cmd_python_version).read()
# print(cmd_python_version)
# print(res_python_version)

