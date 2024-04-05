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
