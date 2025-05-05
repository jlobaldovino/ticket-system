import { IsString, IsUUID, IsDate } from 'class-validator';
import { Type } from 'class-transformer';

export class EventoAuditoriaDTO {
  @IsUUID()
  id!: string;

  @IsString()
  descripcion!: string;

  @IsDate()
  @Type(() => Date)
  fecha!: Date;

  @IsString()
  usuarioId!: string;
}