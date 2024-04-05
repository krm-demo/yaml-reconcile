# yaml-reconcile
Java-Library to process and reconcile data in YAML format

Some Markdown text with <span style="color:blue">some blue text</span>.
And this a normal test again.

And what about table
<table>
  <tr>
    <th>Company</th>
    <th>Contact</th>
    <th>Country</th>
  </tr>
  <tr>
    <td>Alfreds Futterkiste</td>
    <td>Maria Anders</td>
    <td>Germany</td>
  </tr>
  <tr>
    <td>Centro comercial Moctezuma</td>
    <td>Francisco Chang</td>
    <td>Mexico</td>
  </tr>
</table>

<hr/>

RED

<div style="font-family:courier; color:red; text-align :center">
    This one is <b>red</b> and centered
</div>

<div style="border:1px; border-color:black">
    <div style="font-family:courier; color:blue; width:30%;">
        This one is <b>blue</b> and left-aligned
    </div>
</div>

<hr/>

Hello! this is the trest commit and bellow there is some Python code:
```python
from pathlib import Path
from rich.console import Console
from typing import Dict

import rich.repr

@rich.repr.auto
class FileBase64:
    def __init__(self, path: Path):
        self.path = path
        self.parent = path.parent
        self.name = self.path.name
        self.full_name = str(self.path)
        self.parent_name = str(self.parent)
        self.sep = self.full_name[len(self.parent_name)]
    def __str__(self):
        return self.full_name

console = Console(width=160, record=False)
path_ctx: Path = Path(__file__).parent
console.rule(f"working-context path: [bold]{path_ctx}[/]")
console.print(FileBase64(path_ctx))

dict_fb64: Dict[str,FileBase64] = { str(file_path): FileBase64(file_path) for file_path in path_ctx.glob("*.*") }
console.print(f"[cyan]{len(dict_fb64)}[/cyan] files found:")
console.print("\n".join([f"[cyan]- [white]{fb.parent_name}{fb.sep}[cyan]{fb.name}[/]" for fb in dict_fb64.values()]))
```
