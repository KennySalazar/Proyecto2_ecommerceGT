import { Pipe, PipeTransform } from '@angular/core';
import { environment } from '../../../environments/environment';

@Pipe({ name: 'imgUrl', standalone: true })
export class ImgUrlPipe implements PipeTransform {
  transform(value: string | null): string {
    if (!value) return 'assets/noimg.png';
    const u = value.startsWith('/uploads/') ? value : '/uploads/' + value.replace(/^\/+/, '');
    return u;
  }
}
